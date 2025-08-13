package com.art.artsea.controller;

import com.art.artsea.model.Artwork;
import com.art.artsea.model.Auction;
import com.art.artsea.model.Category;
import com.art.artsea.model.User;
import com.art.artsea.repository.UserRepository;
import com.art.artsea.service.ArtworkService;
import com.art.artsea.service.AuctionService;
import com.art.artsea.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);
    @Autowired
    private ArtworkService artworkService;

    @GetMapping("/")
    public String showHome(Model model) {

        List<Auction> upcomingAuctions = auctionService.getUpcomingAuctions();
        model.addAttribute("upcomingAuctions", upcomingAuctions);
        return "home";
    }

    @GetMapping("/buy")
    public String showBuy() {
        return "buy";
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

    @GetMapping("/live-auctions")
    public String showLiveAuctions() {
        return "live-auction"; // You can pass model later for filtering
    }

    @GetMapping("/past-auctions")
    public String showPastAuctions() {
        return "auction-results";
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


    // Admin
    @GetMapping("/admin")
    public String adminLoginPage() {
        return "admin/admin-login";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session) {
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
        return "admin/dashboard"; // dashboard template
    }

    @Autowired
    private UserRepository userRepository;

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

        return "admin/manage-auction"; // Category Page template
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

    @GetMapping("/seller-dashboard")
    public String sellerDashboard(HttpSession session) {
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

}
