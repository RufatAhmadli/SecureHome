package example.web.securehome.service;

import example.web.securehome.dto.request.HomeRequestDto;
import example.web.securehome.dto.response.HomeResponseDto;
import example.web.securehome.entity.Home;
import example.web.securehome.exception.custom.HomeNotFoundException;
import example.web.securehome.mapper.HomeMapper;
import example.web.securehome.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final HomeRepository homeRepository;
    private final HomeMapper homeMapper;

    public HomeResponseDto findHome(Long id) {
        Home home = homeRepository.findById(id)
                .orElseThrow(() -> new HomeNotFoundException(id));
        return homeMapper.toHomeResponseDto(home);
    }

    public List<HomeResponseDto> findHomes() {
        return homeRepository.findAll()
                .stream()
                .map(homeMapper::toHomeResponseDto)
                .toList();
    }

    public HomeResponseDto createHome(HomeRequestDto homeRequestDto) {
        Home saved = homeRepository.save(homeMapper.toHomeEntity(homeRequestDto));
        return homeMapper.toHomeResponseDto(saved);
    }

    public HomeResponseDto updateHome(Long id, HomeRequestDto homeRequestDto) {
        Home found = homeRepository.findById(id)
                .orElseThrow(() -> new HomeNotFoundException(id));
        homeMapper.updateHomeEntity(found, homeRequestDto);
        return homeMapper.toHomeResponseDto(homeRepository.save(found));
    }

    public void deleteHome(Long id) {
        homeRepository.deleteById(id);
    }
}
