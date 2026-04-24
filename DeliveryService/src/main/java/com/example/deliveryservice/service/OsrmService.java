package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.SegmentResponse;
import com.example.deliveryservice.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OsrmService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OSRM_URL =
            "https://router.project-osrm.org/route/v1/car/%f,%f;%f,%f?overview=full&geometries=geojson";

    public OsrmRouteResult calculateRoute(
            double sourceLat, double sourceLng,
            double destLat, double destLng) {

        String url = String.format(OSRM_URL, sourceLng, sourceLat, destLng, destLat);
        log.debug("Calling OSRM: {}", url);

        JsonNode response;
        try {
            response = restTemplate.getForObject(url, JsonNode.class);
        } catch (Exception e) {
            log.error("OSRM call failed: {}", e.getMessage());
            throw BusinessException.osrmRouteFailed();
        }

        if (response == null || !response.get("code").asText().equals("Ok")) {
            throw BusinessException.osrmRouteFailed();
        }

        List<double[]> fullPath = extractFullPath(response);
        double totalDistanceKm = extractDistanceKm(response);
        int segmentCount = calculateSegmentCount(totalDistanceKm);
        List<double[]> checkpoints = subdivide(fullPath, segmentCount);

        log.debug("Route calculated: {}km, {} segments", totalDistanceKm, segmentCount);

        return new OsrmRouteResult(totalDistanceKm, segmentCount, fullPath, checkpoints);
    }

    private List<double[]> extractFullPath(JsonNode response) {
        List<double[]> path = new ArrayList<>();
        JsonNode coords = response.get("routes").get(0).get("geometry").get("coordinates");
        for (JsonNode node : coords) {
            // GeoJSON is [lng, lat] — we store as [lat, lng]
            path.add(new double[]{node.get(1).asDouble(), node.get(0).asDouble()});
        }
        return path;
    }

    private double extractDistanceKm(JsonNode response) {
        double distanceMeters = response.get("routes").get(0).get("distance").asDouble();
        return distanceMeters / 1000.0;
    }

    private int calculateSegmentCount(double distanceKm) {
        if (distanceKm < 20.0) {
            return 2;
        }
        return Math.min((int) Math.floor(distanceKm / 20.0), 12);
    }

    private List<double[]> subdivide(List<double[]> path, int n) {
        if (path.size() < 2) return path;

        double[] cumDist = new double[path.size()];
        cumDist[0] = 0;
        for (int i = 1; i < path.size(); i++) {
            cumDist[i] = cumDist[i - 1] + haversineMeters(path.get(i - 1), path.get(i));
        }

        double totalDist = cumDist[cumDist.length - 1];
        double step = totalDist / n;
        List<double[]> result = new ArrayList<>();

        result.add(path.get(0));

        int si = 0;
        for (int k = 1; k < n; k++) {
            double target = step * k;
            while (si < cumDist.length - 2 && cumDist[si + 1] < target) {
                si++;
            }
            double segLen = cumDist[si + 1] - cumDist[si];
            double t = (segLen == 0) ? 0 : (target - cumDist[si]) / segLen;
            double[] a = path.get(si);
            double[] b = path.get(si + 1);
            result.add(new double[]{
                    a[0] + (b[0] - a[0]) * t,
                    a[1] + (b[1] - a[1]) * t
            });
        }

        result.add(path.get(path.size() - 1));
        return result;
    }

    private double haversineMeters(double[] p1, double[] p2) {
        final int R = 6371000;
        double dLat = Math.toRadians(p2[0] - p1[0]);
        double dLng = Math.toRadians(p2[1] - p1[1]);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(p1[0])) * Math.cos(Math.toRadians(p2[0]))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public record OsrmRouteResult(
            double totalDistanceKm,
            int segmentCount,
            List<double[]> fullPath,
            List<double[]> checkpoints
    ) {}
}