package com.art.artsea.controller;

import com.art.artsea.model.Auction;
import com.art.artsea.model.Slider;
import com.art.artsea.model.User;
import com.art.artsea.service.AuctionService;
import com.art.artsea.service.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class SliderController {

    @Autowired
    private SliderService sliderService;

    @Autowired
    private AuctionService auctionService;

    // Admin slider management page
    @GetMapping("/admin-manage-slider")
    public String adminSliderManage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/admin"; // redirect to login
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-admins to homepage
            }
        }

        // Fetch sliders and completed auctions
        List<Slider> sliders = sliderService.getAllSliders();
        List<Auction> completedAuctions = auctionService.getCompletedAuctions();

        model.addAttribute("sliders", sliders);
        model.addAttribute("completedAuctions", completedAuctions);

        return "admin/slider-manage"; // Thymeleaf template
    }

    // Add/Edit slider form page
//    @GetMapping("/slider-form")
//    public String sliderFormPage(Model model) {
//        List<Auction> completedAuctions = auctionService.getCompletedAuctions();
//        model.addAttribute("completedAuctions", completedAuctions);
//        model.addAttribute("slider", new Slider());
//        return "admin/slider-form"; // Thymeleaf template
//    }

    // Save slider
    @PostMapping("/admin-save-slider")
    public String saveSlider(@ModelAttribute Slider slider,
                             @RequestParam("auctionId") Long auctionId,
                             @RequestParam("sliderImage") MultipartFile imageFile) throws IOException {

        // 1. Set the Auction
        Auction auction = auctionService.getAuctionById(auctionId);
        slider.setAuction(auction);

        // 3. Handle image upload
        if (!imageFile.isEmpty()) {
            File uploadDir = new File("uploads/sliders/");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get("uploads/sliders/", fileName);
            Files.write(filePath, imageFile.getBytes());

            slider.setImageUrl("/uploads/sliders/" + fileName);
        }

        // 4. Save the slider
        sliderService.saveSlider(slider);

        // 5. Redirect to admin slider manage page
        return "redirect:/admin-manage-slider";
    }

    // Delete slider
    @GetMapping("/admin-slider-delete/{id}")
    public String deleteSlider(@PathVariable Long id) {
        sliderService.deleteSlider(id);
        return "redirect:/admin-manage-slider";
    }

//    // Fetch completed auctions for dropdown (optional AJAX)
//    @GetMapping("/auctions/completed")
//    @ResponseBody
//    public List<Auction> getCompletedAuctions() {
//        return auctionService.getCompletedAuctions();
//    }
}
