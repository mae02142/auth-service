package com.bookpli.auth_service.controller;

import com.bookpli.auth_service.service.CustomPrincipal;
import com.bookpli.auth_service.service.SpotifyAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spotify")
@Slf4j
public class SpotifyController {

    private final SpotifyAuthService spotifyAuthService;

    @GetMapping("/me/playlists")
    public Map<String, Object> getUserPlaylists() {
        System.out.println("SpotifyController 로 도착은 함!!!!!!!!!!");
        return spotifyAuthService.getUserPlaylists();
    }

    @GetMapping("/albums/{albumId}/tracks")
    public Map<String, Object> getAlbumTracks(@PathVariable String albumId) {
        return spotifyAuthService.getAlbumTracks(albumId);
    }

    @GetMapping("/playlists/{playlistId}/tracks")
    public Map<String, Object> getPlaylistWithMusic(@PathVariable String playlistId) {
        return spotifyAuthService.getPlaylistWithMusic(playlistId);
    }

    @PostMapping("/users/{spotifyId}/playlists")
    public Map<String, Object> createPlaylist(@RequestBody Map<String, Object> request) {
        return spotifyAuthService.createPlaylist(request);
    }

    @PutMapping("/playlists/{playlistId}")
    public void updatePlaylistTitle(@PathVariable String playlistId, @RequestBody Map<String, String> request) {
        spotifyAuthService.updatePlaylistTitle(playlistId, request);
    }

    @DeleteMapping("/playlists/{playlistId}/followers")
    public void deletePlaylist(@PathVariable String playlistId) {
        spotifyAuthService.deletePlaylist(playlistId);
    }

    @PostMapping("/playlists/{playlistId}/tracks")
    public void addPlaylist(@PathVariable String playlistId, @RequestBody Map<String, Object> request) {
        spotifyAuthService.addPlaylist(playlistId, request);
    }

    @DeleteMapping("/playlists/{playlistId}/tracks")
    public void deleteTrack(@PathVariable String playlistId, @RequestBody Map<String, Object> request) {
        spotifyAuthService.deleteTrack(playlistId, request);
    }
}
