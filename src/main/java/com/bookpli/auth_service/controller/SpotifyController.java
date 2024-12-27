package com.bookpli.auth_service.controller;

import com.bookpli.auth_service.client.SpotifyApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spotify")
public class SpotifyController {

    private final SpotifyApiClient spotifyApiClient;

    @GetMapping("/me/playlists")
    public Map<String, Object> getUserPlaylists() {
        return spotifyApiClient.sendGetRequest("/me/playlists");
    }

    @GetMapping("/albums/{albumId}/tracks")
    public Map<String, Object> getAlbumTracks(@PathVariable String albumId) {
        String endPoint = "/albums/" + albumId + "/tracks";
        return spotifyApiClient.sendGetRequest(endPoint);
    }

    @GetMapping("/playlists/{playlistId}/tracks")
    public Map<String, Object> getPlaylistWithMusic(@PathVariable String playlistId) {
        String endpoint = "/playlists/" + playlistId + "/tracks";
        return spotifyApiClient.sendGetRequest(endpoint);
    }

    @PostMapping("/users/{spotifyId}/playlists")
    public Map<String, Object> createPlaylist(@PathVariable String spotifyId, @RequestBody Map<String, Object> request) {
        String endPoint = "/users/" + spotifyId + "/playlists";
        return spotifyApiClient.sendPostRequest(endPoint, request);
    }

    @PutMapping("/playlists/{playlistId}")
    public void updatePlaylistTitle(@PathVariable String playlistId, @RequestBody Map<String, String> request) {
        String endPoint = "/playlists/" + playlistId;
        spotifyApiClient.sendPutRequest(endPoint, request);
    }

    @DeleteMapping("/playlists/{playlistId}/followers")
    public void deletePlaylist(@PathVariable String playlistId) {
        String endPoint = "/playlists/" + playlistId + "/followers";
        spotifyApiClient.sendDeleteRequestWithoutBody(endPoint);
    }

    @PostMapping("/playlists/{playlistId}/tracks")
    public void addPlaylist(@PathVariable String playlistId, @RequestBody Map<String, Object> request) {
        String endPoint = "/playlists/" + playlistId + "/tracks";
        spotifyApiClient.sendPostRequest(endPoint, request);
    }

    @DeleteMapping("/playlists/{playlistId}/tracks")
    public void deleteTrack(@PathVariable String playlistId, @RequestBody Map<String, Object> track) {
        Map<String, Object> request = Map.of(
                "tracks", List.of(Map.of("uri", track.get("uri")))
        );
        String endPoint = "/playlists/" + playlistId + "/tracks";
        spotifyApiClient.sendDeleteRequest(endPoint, request);
    }
}

