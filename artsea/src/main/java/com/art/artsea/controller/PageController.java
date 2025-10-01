package com.art.artsea.controller;

import com.art.artsea.model.*;
import com.art.artsea.repository.*;
import com.art.artsea.service.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);
    @Autowired
    private ArtworkService artworkService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private ArtworkRepository artworkRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private BidService bidService;
    @Autowired
    private UserService userService;

    @Autowired
    private SliderService sliderService;

    @GetMapping("/")
    public String showHome(Model model) {

        List<Auction> liveAuctions = auctionService.getLiveAuctions();
        model.addAttribute("liveAuctions", liveAuctions);

        List<Auction> upcomingAuctions = auctionService.getUpcomingAuctions();
        model.addAttribute("upcomingAuctions", upcomingAuctions);

        List<Auction> pastAuctions = auctionService.getPastAuctions();
        model.addAttribute("pastAuctions", pastAuctions);

        //  Fetch sliders
        List<Slider> sliders = sliderService.getAllSliders();
        model.addAttribute("sliders", sliders);

        return "home";
    }

    @GetMapping("/search")
    public String searchArtworks(@RequestParam("query") String query, Model model) {
        // Fetch artworks that match title and are APPROVED
        List<Artwork> artworks = artworkRepository
                .findByTitleContainingIgnoreCaseAndStatus(query, Artwork.ArtworkStatus.APPROVED);

        // Loop artworks and check if they have bids (to mark sold/unsold)
        for (Artwork artwork : artworks) {
            List<Bid> bids = bidRepository.findTopBidByArtworkId(artwork.getArtworkId());

            if (bids != null && !bids.isEmpty()) {
                Bid highestBid = bids.get(0);  // top bid
                artwork.setSold(true);
                artwork.setHighestBid(highestBid.getBidAmount().doubleValue());
                artwork.setHighestBidderName(highestBid.getUser().getUsername());
            } else {
                artwork.setSold(false);
            }
        }

        model.addAttribute("artworks", artworks);
        model.addAttribute("query", query);

        return "search-results";
    }




    @GetMapping("/my-profile")
    public String showProfile(Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        if (user instanceof User loggedInUser) {
            if (!"BUYER".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/";
            }

            model.addAttribute("user", loggedInUser);

            LocalDateTime now = LocalDateTime.now();

            // ------------------ Fetch Live Auction Bids ------------------
            Map<Long, Bid> latestBidsByArtwork = bidService.getBidsByUser(loggedInUser.getUserId()).stream()
                    .filter(bid -> {
                        Artwork artwork = bid.getArtwork();
                        Auction auction = artwork.getAuction();
                        // only include ongoing auctions
                        return auction.getStartTime().isBefore(now) && auction.getEndTime().isAfter(now);
                    })
                    .collect(Collectors.toMap(
                            bid -> bid.getArtwork().getArtworkId(),
                            bid -> bid,
                            // if multiple bids by same user for same artwork → keep the latest one
                            (bid1, bid2) -> bid1.getBidTime().isAfter(bid2.getBidTime()) ? bid1 : bid2
                    ));

            List<Map<String, Object>> bidsData = latestBidsByArtwork.values().stream().map(bid -> {
                Map<String, Object> data = new HashMap<>();
                Artwork artwork = bid.getArtwork();
                Auction auction = artwork.getAuction();

                double currentHighestBid = bidService.getHighestBidAmountForArtwork(artwork.getArtworkId())
                        .orElse(artwork.getStartingPrice());

                boolean isWinning = bid.getBidAmount().doubleValue() == currentHighestBid;

                data.put("artwork", artwork);
                data.put("userBid", bid.getBidAmount());
                data.put("status", isWinning ? "Winning" : "Outbid");
                data.put("auction", auction);

                return data;
            }).toList();

            model.addAttribute("bidsData", bidsData);

            // ------------------ All Bids (history, DESC) ------------------
            List<Map<String, Object>> allBids = bidService.getBidsByUser(loggedInUser.getUserId()).stream()
                    .sorted(Comparator.comparing(Bid::getBidTime).reversed()) // latest first
                    .map(bid -> {
                        Map<String, Object> data = new HashMap<>();
                        Artwork artwork = bid.getArtwork();
                        Auction auction = artwork.getAuction();

                        double currentHighestBid = bidService.getHighestBidAmountForArtwork(artwork.getArtworkId())
                                .orElse(artwork.getStartingPrice());

                        boolean isWinning = bid.getBidAmount().doubleValue() == currentHighestBid;

                        data.put("artwork", artwork);
                        data.put("userBid", bid.getBidAmount());
                        data.put("status", isWinning ? "Winning" : "Outbid");
                        data.put("auction", auction);
                        data.put("bidTime", bid.getBidTime()); // helpful for UI

                        return data;
                    })
                    .toList();

            model.addAttribute("allBids", allBids);



            // ------------------ Pending Payments (won auctions) ------------------
            List<Map<String, Object>> pendingPayments = bidService.getBidsByUser(loggedInUser.getUserId()).stream()
                    .filter(bid -> {
                        Artwork artwork = bid.getArtwork();
                        Auction auction = artwork.getAuction();
                        boolean auctionEnded = auction.getEndTime().isBefore(now);

                        double highest = bidService.getHighestBidAmountForArtwork(artwork.getArtworkId())
                                .orElse(artwork.getStartingPrice());

                        boolean isWinningBid = bid.getBidAmount().doubleValue() == highest;

                        // Check if payment already done
                        boolean paymentPending = orderRepository.findByBid_BidId(bid.getBidId())
                                .stream()
                                .noneMatch(order ->
                                        "SUCCESS".equalsIgnoreCase(order.getStatus()) ||
                                                "PROCESSED".equalsIgnoreCase(order.getStatus())
                                );


                        return auctionEnded && isWinningBid && paymentPending;
                    })
                    .map(bid -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("bidId", bid.getBidId());
                        data.put("artwork", bid.getArtwork());
                        data.put("finalBid", bid.getBidAmount());
                        data.put("auction", bid.getArtwork().getAuction());
                        data.put("paymentStatus", "Pending");
                        return data;
                    })
                    .toList();

            // ------------------ Won Auctions (Paid orders) ------------------
            List<Map<String, Object>> wonOrders = orderRepository.findByUser_UserId(loggedInUser.getUserId()).stream()
                    .filter(order -> "SUCCESS".equalsIgnoreCase(order.getStatus())
                            || "PROCESSED".equalsIgnoreCase(order.getStatus()))
                    .map(order -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("artwork", order.getBid().getArtwork());
                        data.put("finalBid", order.getAmount());
                        data.put("auction", order.getBid().getArtwork().getAuction());
                        data.put("status", order.getStatus());
                        data.put("trackingId", order.getTrackingId()); // assuming trackingId column
                        return data;
                    })
                    .toList();

            // Total number of bids
            long totalBids = bidService.getBidsByUser(loggedInUser.getUserId()).size();

            // Total number of won auctions (paid orders)
            long totalWon = wonOrders.size();

            model.addAttribute("totalBids", totalBids);
            model.addAttribute("totalWon", totalWon);


            model.addAttribute("wonOrders", wonOrders);
            model.addAttribute("pendingPayments", pendingPayments);
        }

        return "my-profile";
    }




    @PostMapping("/my-profile/save")
    public String saveProfile(User formUser, HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");

        // Update DB
        userService.updateProfile(sessionUser.getUserId(),
                formUser.getUsername(),
                formUser.getPhone());

//        // Refresh user from DB
//        User updatedUser = userService.findById(sessionUser.getUserId());
//        session.setAttribute("user", updatedUser);
//
//        // Add updated user to model
//        model.addAttribute("user", updatedUser);

        return "my-profile";
    }





    @GetMapping("/buy")
    public String showBuy() {
        return "buy";
    }
    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "buyer-reset-password";
    }

    @GetMapping("/sell")
    public String showSell() {
        return "sell";
    }

    @GetMapping("/cookie-policy")
    public String showCookiePolicy() {
        return "cookie-policy";
    }

    @GetMapping("/faqs")
    public String showFaqs() {
        return "faqs";
    }

    @GetMapping("/privacy-policy")
    public String showPrivacyPolicy() {
        return "privacy-policy";
    }

    @GetMapping("/reach-us")
    public String showReachUs() {
        return "reach-us";
    }

    @GetMapping("/terms-and-conditions")
    public String showTermsAndConditions() {
        return "terms-and-conditions";
    }

    @GetMapping("/who-we-are")
    public String showWhoWeAre() {
        return "who-we-are";
    }

    @GetMapping("/auctions")
    public String showAuctions() {
        return "auction-results";
    }

    @GetMapping("/record-price-artwork")
    public String showRecordPriceArtwork() {
        return "auction-results";
    }

    @GetMapping("/auctions-result")
    public String showAuctionsResult() {
        return "auction-results";
    }

//    @GetMapping("/live-auctions")
//    public String showLiveAuctions() {
//        return "live-auction"; // You can pass model later for filtering
//    }

    @GetMapping("/past-auctions")
    public String showPastAuctions(Model model) {
        List<Auction> pastAuctions = auctionService.getPastAuctions();
        List<Object[]> totals = auctionRepository.findPastAuctionTotals();

        Map<Long, Double> totalSales = new HashMap<>();
        for (Object[] row : totals) {
            Long auctionId = (Long) row[0];
            Double totalSale = ((Number) row[1]).doubleValue();
            totalSales.put(auctionId, totalSale);
        }

        model.addAttribute("pastAuctions", pastAuctions);
        model.addAttribute("totalSales", totalSales);
        return "auction-results";
    }

    // Show single past auction details
    @GetMapping("/past-auction/{auctionId}")
    public String showPastAuctionDetails(@PathVariable Long auctionId, Model model) {
        Auction auction = auctionRepository.findById(auctionId).orElse(null);
        if (auction == null) {
            return "redirect:/past-auctions";
        }

        List<Artwork> artworks = artworkRepository.findByAuctionAuctionId(auctionId);

        for (Artwork artwork : artworks) {
            List<Bid> bids = bidRepository.findTopBidByArtworkId(artwork.getArtworkId());

            if (bids != null && !bids.isEmpty()) {
                Bid highestBid = bids.get(0);
                artwork.setSold(true);
                artwork.setHighestBid(highestBid.getBidAmount().doubleValue());
                artwork.setHighestBidderName(highestBid.getUser().getUsername()); // Assuming User entity has getUsername()
            } else {
                artwork.setSold(false);
            }
        }

        model.addAttribute("auction", auction);
        model.addAttribute("artworks", artworks);

        return "past-auction";
    }

    



    @GetMapping("/upcoming-auctions")
    public String showUpcomingAuctions(Model model) {

        model.addAttribute("auctions", auctionService.getUpcomingAuctions());
        return "upcoming-auctions";
    }

    @GetMapping("/private-services")
    public String showPrivateServices() {
        return "private-services";
    }

    @GetMapping("/storage-services")
    public String showStorageServices() {
        return "storage-services";
    }

    @GetMapping("/collection-services")
    public String showCollectionServices() {
        return "collection-services";
    }

    @GetMapping("/post-sale-services")
    public String showPostSaleServices() {
        return "post-sales-services";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignUp() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    @GetMapping("/upcoming-preview/{auctionId}")
    public String upcomingPreview(@PathVariable Long auctionId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Auction> optionalAuction = auctionRepository.findById(auctionId);

        if (optionalAuction.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Auction not found!");
            return "redirect:/"; // redirect home
        }

        Auction auction = optionalAuction.get();
        LocalDateTime now = LocalDateTime.now();

        // Check if current time is within allowed preview time
        if (now.isBefore(auction.getApplyEndTime()) || now.isAfter(auction.getStartTime())) {
            redirectAttributes.addFlashAttribute("error", "Preview not available at this time.");
            return "redirect:/"; // redirect home
        }

        // Fetch all approved artworks of this auction
        List<Artwork> artworks = artworkService.getApprovedArtworksByAuctionId(auctionId);

//        model.addAttribute("auction", auction);
        model.addAttribute("artworks", artworks);
        return "upcoming-auction-preview"; // Thymeleaf template
    }




    // Admin
    @GetMapping("/admin")
    public String adminLoginPage() {
        return "admin/admin-login";
    }

    private final DashboardService dashboardService;

    // Constructor injection
    public PageController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    @GetMapping("/admin-dashboard")
    public String adminDashboard(Model model,HttpSession session) {
        Object user = session.getAttribute("user"); // or "user" depending on your login
        if (user == null) {
            return "redirect:/admin"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }
        // Users
        model.addAttribute("totalBuyers", dashboardService.getTotalBuyers());
        model.addAttribute("totalSellers", dashboardService.getTotalSellers());
        model.addAttribute("activeUsers", dashboardService.getActiveUsers());
        model.addAttribute("pendingUsers", dashboardService.getPendingUsers());

        // Auctions
        model.addAttribute("liveAuctions", dashboardService.getLiveAuctionCount());
        model.addAttribute("pastAuctions", dashboardService.getPastAuctionCount());
        model.addAttribute("upcomingAuctions", dashboardService.getUpcomingAuctionCount());

        // Artworks
        model.addAttribute("approvedArtworks", dashboardService.getApprovedArtworkCount());
        model.addAttribute("pendingArtworks", dashboardService.getPendingArtworkCount());

        // Categories
        model.addAttribute("totalCategories", dashboardService.getTotalCategories());

        //Order Amounts
        // Revenue & Highest Order
        model.addAttribute("totalRevenue", dashboardService.getTotalRevenue());
        model.addAttribute("highestOrder", dashboardService.getHighestOrderAmount());

        return "admin/dashboard"; // dashboard template
    }

    @GetMapping("/admin-artwork-status")
    public String adminArtworkStatus(HttpSession session, Model model) {

        // Check if admin is logged in
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return "redirect:/admin"; // redirect to admin login
        }

        if (userObj instanceof User) {
            User loggedInUser = (User) userObj;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }

        // Fetch artwork status list
        List<Object[]> artworkStatusList = artworkService.getAllArtworkStatus();

        // Pass data to template
        model.addAttribute("artworks", artworkStatusList);
//        model.addAttribute("totalArtworks", artworkStatusList.size());

        return "admin/artwork-status"; // Thymeleaf template
    }




    @GetMapping("/admin-verify-users")
    public String adminUserVerification(HttpSession session, Model model) { // ✅ Pass Model as parameter
        Object userObj = session.getAttribute("user");

        if (userObj == null) {
            return "redirect:/admin"; // redirect to login
        }

        if (userObj instanceof User) {
            User loggedInUser = (User) userObj;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }

        List<User> unverifiedUsers = userRepository.findByIsVerified(0);
        model.addAttribute("users", unverifiedUsers);
        model.addAttribute("totalPending", unverifiedUsers.size());

        return "admin/user-verification";
    }
    @GetMapping("/admin-add-category")
    public String adminAddCategory(HttpSession session) {
        Object user = session.getAttribute("user"); // or "user" depending on your login
        if (user == null) {
            return "redirect:/admin"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }
        return "admin/add-category"; // Category Page template
    }
    @GetMapping("/admin-add-auction")
    public String adminAddAuction(HttpSession session) {
        Object user = session.getAttribute("user"); // or "user" depending on your login
        if (user == null) {
            return "redirect:/admin"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }
        return "admin/add-auction"; // Category Page template
    }

    @Autowired
    private CategoryService categoryService;
    @GetMapping("/admin-manage-category")
    public String adminManageCategory(HttpSession session, Model model) {
        Object user = session.getAttribute("user"); // or "user" depending on your login
        if (user == null) {
            return "redirect:/admin"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }

        // Fetch categories dynamically
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "admin/manage-category"; // Category Page template
    }

    //Display Auctions and Mange
    @Autowired
    private AuctionService auctionService;
    @GetMapping("/admin-manage-auction")
    public String adminManageAuction(HttpSession session, Model model) {
        Object user = session.getAttribute("user"); // or "user" depending on your login
        if (user == null) {
            return "redirect:/admin"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }

        // Fetch auctions dynamically
        List<Auction> auctions = auctionService.getAllAuctions();
        model.addAttribute("auctions", auctions);
        model.addAttribute("now", java.time.LocalDateTime.now());
        return "admin/manage-auction"; // Category Page template
    }
    @GetMapping("/admin-edit-auction/{id}")
    public String adminEditAuction(@PathVariable("id") Long id,HttpSession session, Model model) {
        Object user = session.getAttribute("user"); // or "user" depending on your login
        if (user == null) {
            return "redirect:/admin"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        // Fetch auction by id for edit dynamically
        Auction auction = auctionService.getAuctionById(id);
        model.addAttribute("auction", auction);
        model.addAttribute("applyStartTimeFormatted", auction.getApplyStartTime().format(formatter));
        model.addAttribute("applyEndTimeFormatted", auction.getApplyEndTime().format(formatter));
        model.addAttribute("startTimeFormatted", auction.getStartTime().format(formatter));
        model.addAttribute("endTimeFormatted", auction.getEndTime().format(formatter));
        return "admin/edit-auction"; // Edit Auction Pagee
    }

    @GetMapping("/admin-manage-artwork")
    public String adminManageArtwork(HttpSession session, Model model) {
        Object user = session.getAttribute("user"); // or "user" depending on your login
        if (user == null) {
            return "redirect:/admin"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }

        // Fetch auctions dynamically
        List<Artwork> artworks = artworkService.getAllAuctions();
        model.addAttribute("artworks", artworks);

        return "admin/manage-artwork-application"; // Category Page template
    }
    @GetMapping("/admin-view-artwork/{id}")
    public String viewArtwork(@PathVariable("id") Long id, Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/admin";
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/";
            }
        }

        Artwork artwork = artworkService.getArtworkById(id);
        model.addAttribute("artwork", artwork);
        return "admin/view-artwork";
    }
    //admin view auctions
    @GetMapping("/admin-view-auction/{id}")
    public String viewAuction(@PathVariable("id") Long id, Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/admin";
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/";
            }
        }
        Auction auction =auctionService.getAuctionById(id);
        model.addAttribute("auction", auction);
        return "admin/admin-view-auction";
    }

    @GetMapping("/admin-accept-artwork/{id}")
    public String acceptArtwork(@PathVariable Long id,HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/admin";
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/";
            }
        }
        Artwork artwork = artworkService.getArtworkById(id); // Fetch from service
        if (artwork != null) {
            artwork.setStatus(Artwork.ArtworkStatus.APPROVED); // Set to APPROVED
            artworkService.saveArtwork(artwork); // Save back to DB
        }
        return "redirect:/admin-manage-artwork"; // Redirect to list page
    }

    @PostMapping("/admin-reject-artwork/{id}")
    public String rejectArtwork(@PathVariable Long id,
                                @RequestParam String rejectionReason,HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/admin";
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/";
            }
        }
        artworkService.rejectArtwork(id, rejectionReason);
        return "redirect:/admin-manage-artwork";
    }







    @PostMapping("/admin/verify-user")
    public String verifyUser(@RequestParam("email") String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsVerified(1); // or true if boolean
            userRepository.save(user);
        }
        return "redirect:/admin-verify-users"; // or your page URL
    }

    @PostMapping("/admin/reject-user")
    @Transactional
    public String rejectUser(@RequestParam("email") String email) {
        userRepository.deleteByEmail(email);
        return "redirect:/admin-verify-users"; // or your page URL
    }

    //Seller
    @GetMapping("/seller")
    public String sellerLoginPage() {
        return "seller/seller-login";
    }
    @GetMapping("/seller-forgot-password")
    public String sellerForgotPasswordPage() {
        return "seller/reset-password";
    }

    @GetMapping("/seller-dashboard")
    public String sellerDashboard(Model model,HttpSession session) {
        Object user = session.getAttribute("user"); // or "user" depending on your login
        if (user == null) {
            return "redirect:/seller"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"SELLER".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
            // Auctions
            model.addAttribute("liveAuctions", dashboardService.getLiveAuctionCount());
            model.addAttribute("pastAuctions", dashboardService.getPastAuctionCount());
            model.addAttribute("upcomingAuctions", dashboardService.getUpcomingAuctionCount());
            model.addAttribute("appliedAuctions",
                    dashboardService.getAppliedAuctions(loggedInUser.getUserId()));

            //Artworks
            model.addAttribute("liveArtworks", dashboardService.getLiveArtworks(loggedInUser.getUserId()));
            model.addAttribute("approvedArtworks", dashboardService.getSellerApprovedArtworks(loggedInUser.getUserId()));
            model.addAttribute("pendingArtworks", dashboardService.getSellerPendingArtworks(loggedInUser.getUserId()));

            //for Sold Artwoks or Sales
            // Count processed orders for this seller
            long processedCount = orderRepository.findAll()
                    .stream()
                    .filter(order ->
                            order.getBid().getArtwork().getUser().getUserId()
                                    .equals(loggedInUser.getUserId())
                                    && "PROCESSED".equalsIgnoreCase(order.getStatus())
                    )
                    .count();

            model.addAttribute("processedCount", processedCount);
        }





        return "seller/dashboard"; // dashboard template
    }

    @GetMapping("/seller-upcoming-auctions")
    public String sellerUpcomingAuctions(HttpSession session, Model model) {
        Object user = session.getAttribute("user"); // or "user" depending on your login
        if (user == null) {
            return "redirect:/seller"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"SELLER".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }
        // Fetch auctions dynamically
        List<Auction> auctions = auctionService.getAllAuctions();
        model.addAttribute("auctions", auctions);

        return "seller/upcoming-auctions";
    }

    @GetMapping("/artwork-applications")
    public String sellerArtworkApplications(HttpSession session, Model model) {
        Object user = session.getAttribute("user"); // or "user" depending on your login
        if (user == null) {
            return "redirect:/seller"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"SELLER".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }
        // Fetch artworks dynamically
        if (user instanceof User) {
            Long userId = ((User) user).getUserId();
            List<Artwork> artworks = artworkService.getArtworkByUserId(userId);
            model.addAttribute("artworks", artworks);
        }

        return "seller/application-status";
    }


    @GetMapping("/live-auctions")
    public String showLiveAuctions(Model model) {
        List<Auction> auctions = auctionService.getLiveAuctions();

        // Map to hold auctionId -> approvedArtworkCount
        Map<Long, Long> artworkCounts = new HashMap<>();

        auctions.forEach(auction -> {
            long count = artworkRepository.countApprovedArtworksByAuctionId(auction.getAuctionId());
            artworkCounts.put(auction.getAuctionId(), count);
        });

        model.addAttribute("auctions", auctions);
        model.addAttribute("artworkCounts", artworkCounts);
        return "live-auctions";
    }

    @GetMapping("/live-auction/{auctionId}")
    public String showAuctionDetails(@PathVariable Long auctionId, Model model) {
        Auction auction = auctionService.getAuctionById(auctionId);

        // ✅ Check if current time is within auction time
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
            // Redirect back if auction not live
            return "redirect:/live-auctions";
        }

        List<Artwork> artworks = artworkRepository.findByAuctionIdAndStatus(
                auctionId,
                Artwork.ArtworkStatus.APPROVED
        );

        List<Map<String, Object>> artworkData = artworks.stream().map(artwork -> {
            Map<String, Object> data = new HashMap<>();

            double currentBid = bidService.getCurrentBidForArtwork(artwork.getArtworkId())
                    .orElse(artwork.getStartingPrice());

            double nextBid = (currentBid == artwork.getStartingPrice())
                    ? currentBid
                    : Math.round(currentBid * 1.1 * 100.0) / 100.0;

            String highestBidderName = bidService.getHighestBidderName(artwork.getArtworkId())
                    .orElse("No bids yet");

            data.put("artwork", artwork);
            data.put("currentBid", currentBid);
            data.put("nextBid", nextBid);
            data.put("highestBidderName", highestBidderName);

            return data;
        }).toList();

        model.addAttribute("auction", auction);
        model.addAttribute("artworks", artworkData);

        return "live-auction"; // page only shown if auction is live
    }







}
