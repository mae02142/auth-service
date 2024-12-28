package com.bookpli.auth_service.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "spotifyFeignClient", url = "https://api.spotify.com/v1")
public interface SpotifyFeignClient {

    @GetMapping("/me/playlists")
    Map<String, Object> getUserPlaylists(@RequestHeader("Authorization") String authHeader);

    @GetMapping("/albums/{albumId}/tracks")
    Map<String, Object> getAlbumTracks(
            @PathVariable("albumId") String albumId,
            @RequestHeader("Authorization") String authHeader
    );

    @GetMapping("/playlists/{playlistId}/tracks")
    Map<String, Object> getPlaylistWithMusic(
            @PathVariable("playlistId") String playlistId,
            @RequestHeader("Authorization") String authHeader
    );

    @PostMapping("/users/{spotifyId}/playlists")
    Map<String, Object> createPlaylist(
            @PathVariable("spotifyId") String spotifyId,
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader
    );

    @PutMapping("/playlists/{playlistId}")
    void updatePlaylistTitle(
            @PathVariable("playlistId") String playlistId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader
    );

    @DeleteMapping("/playlists/{playlistId}/followers")
    void deletePlaylist(
            @PathVariable("playlistId") String playlistId,
            @RequestHeader("Authorization") String authHeader
    );

    @PostMapping("/playlists/{playlistId}/tracks")
    void addPlaylist(
            @PathVariable("playlistId") String playlistId,
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader
    );

    @DeleteMapping("/playlists/{playlistId}/tracks")
    void deleteTrack(
            @PathVariable("playlistId") String playlistId,
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader
    );
}
