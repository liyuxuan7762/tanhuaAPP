package com.tanhua.server.controller;

import com.tanhua.server.service.BaiduService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/baidu")
public class BaiduController {
    @Resource
    private BaiduService baiduService;

    /**
     * 上报地理位置信息
     * @param map
     * @return
     */
    @PostMapping("/location")
    public ResponseEntity uploadLocation(@RequestBody Map map) {
        double latitude = Double.parseDouble(map.get("latitude").toString());
        double longitude = Double.parseDouble(map.get("longitude").toString());
        String addrStr = map.get("addrStr").toString();
        this.baiduService.uploadLocation(latitude, longitude, addrStr);
        return ResponseEntity.ok(null);
    }
}
