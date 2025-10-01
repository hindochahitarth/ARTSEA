package com.art.artsea.service;

import com.art.artsea.model.Auction;
import com.art.artsea.model.Slider;
import com.art.artsea.repository.AuctionRepository;
import com.art.artsea.repository.SliderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class SliderService {

    @Autowired
    private SliderRepository sliderRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    private final String uploadDir = "uploads/sliders/";

    public List<Slider> getAllSliders() {
        return sliderRepository.findAll();
    }

    public Optional<Slider> getSliderById(Long id) {
        return sliderRepository.findById(id);
    }

    public void saveSlider(Slider slider) {
        sliderRepository.save(slider);
    }

    public void deleteSlider(Long id) {
        sliderRepository.deleteById(id);
    }
}
