package com.bookpli.auth_service.service;

import com.bookpli.auth_service.controller.SpotifyFeignClient;
import com.bookpli.auth_service.manager.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyAuthService {

    private final SpotifyFeignClient spotifyFeignClient;
    private final TokenManager tokenManager;

    /**
     * Spotify API: 사용자 Playlists 조회
     */
    public Map<String, Object> getUserPlaylists() {
        String spotifyId = getSpotifyIdFromContext();
        log.info("SpotifyAuthService - getUserPlaylists: spotifyId={}", spotifyId);
        return spotifyFeignClient.getUserPlaylists(getAuthHeader(spotifyId));
    }

    /**
     * Spotify API: 앨범 트랙 조회
     */
    public Map<String, Object> getAlbumTracks(String albumId) {
        String spotifyId = getSpotifyIdFromContext();
        log.info("SpotifyAuthService - getAlbumTracks: spotifyId={}, albumId={}", spotifyId, albumId);
        return spotifyFeignClient.getAlbumTracks(albumId, getAuthHeader(spotifyId));
    }

    /**
     * Spotify API: 특정 Playlist 조회
     */
    public Map<String, Object> getPlaylistWithMusic(String playlistId) {
        String spotifyId = getSpotifyIdFromContext();
        log.info("SpotifyAuthService - getPlaylistWithMusic: spotifyId={}, playlistId={}", spotifyId, playlistId);
        return spotifyFeignClient.getPlaylistWithMusic(playlistId, getAuthHeader(spotifyId));
    }

    /**
     * Spotify API: 새로운 Playlist 생성
     */
    public Map<String, Object> createPlaylist(Map<String, Object> request) {
        String spotifyId = getSpotifyIdFromContext();
        log.info("SpotifyAuthService - createPlaylist: spotifyId={}", spotifyId);
        return spotifyFeignClient.createPlaylist(spotifyId, request, getAuthHeader(spotifyId));
    }

    /**
     * Spotify API: Playlist 이름 업데이트
     */
    public void updatePlaylistTitle(String playlistId, Map<String, String> request) {
        String spotifyId = getSpotifyIdFromContext();
        log.info("SpotifyAuthService - updatePlaylistTitle: spotifyId={}, playlistId={}", spotifyId, playlistId);
        spotifyFeignClient.updatePlaylistTitle(playlistId, request, getAuthHeader(spotifyId));
    }

    /**
     * Spotify API: Playlist 삭제
     */
    public void deletePlaylist(String playlistId) {
        String spotifyId = getSpotifyIdFromContext();
        log.info("SpotifyAuthService - deletePlaylist: spotifyId={}, playlistId={}", spotifyId, playlistId);
        spotifyFeignClient.deletePlaylist(playlistId, getAuthHeader(spotifyId));
    }

    /**
     * Spotify API: Playlist에 트랙 추가
     */
    public void addPlaylist(String playlistId, Map<String, Object> request) {
        String spotifyId = getSpotifyIdFromContext();
        log.info("SpotifyAuthService - addPlaylist: spotifyId={}, playlistId={}", spotifyId, playlistId);
        spotifyFeignClient.addPlaylist(playlistId, request, getAuthHeader(spotifyId));
    }

    /**
     * Spotify API: Playlist에서 트랙 삭제
     */
    public void deleteTrack(String playlistId, Map<String, Object> request) {
        String spotifyId = getSpotifyIdFromContext();
        log.info("SpotifyAuthService - deleteTrack: spotifyId={}, playlistId={}", spotifyId, playlistId);
        spotifyFeignClient.deleteTrack(playlistId, request, getAuthHeader(spotifyId));
    }

    /**
     * SecurityContext에서 Spotify ID 추출
     */
    private String getSpotifyIdFromContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomPrincipal) {
            String spotifyId = ((CustomPrincipal) principal).getSpotifyId();
            log.debug("Spotify ID retrieved from SecurityContext: {}", spotifyId);
            return spotifyId;
        }
        throw new IllegalStateException("Unauthenticated user.");
    }

    /**
     * Spotify API 호출을 위한 Authorization Header 생성
     */
    private String getAuthHeader(String spotifyId) {
        String accessToken = tokenManager.getAccessToken(spotifyId);
        log.debug("Access Token retrieved for Spotify ID {}: {}", spotifyId, accessToken);
        return "Bearer " + accessToken;
    }
}
