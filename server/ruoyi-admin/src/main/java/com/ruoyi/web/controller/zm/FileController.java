package com.ruoyi.web.controller.zm;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.cache.Key;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.excel.ExcelResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.zm.domain.DevProtocol01;
import com.ruoyi.zm.domain.vo.Protocol01ExcelImportVo;
import com.ruoyi.zm.mapper.DevProtocol01Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/zm/file")
public class FileController {

    @Resource
    private DevProtocol01Mapper protocol01Mapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @PostMapping(value = "/import01Data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file) throws Exception {
        // ExcelResult<Protocol01ExcelImportVo> excelResult = ExcelUtil.importExcel(file.getInputStream(), Protocol01ExcelImportVo.class, true);
        Map<String, Object> map = ExcelUtil.importExcelByHead(file.getInputStream(), Protocol01ExcelImportVo.class, true);
        Map<Integer, String> head = (Map<Integer, String>) map.get("head");
        if (!"位地址 (十六进制)".equals(head.get(0)) || !"名称".equals(head.get(1))) {
            log.error("FileController → importData，文件格式不正确！！当前文件格式：{}", head);
            throw new RuntimeException("文件格式不正确！！");
        }
        List<Protocol01ExcelImportVo> volist = ((ExcelResult<Protocol01ExcelImportVo>) map.get("listener")).getList();
        List<DevProtocol01> list = BeanUtil.copyToList(volist, DevProtocol01.class);
        protocol01Mapper.deleteAll();
        protocol01Mapper.insertBatch(list);
        redisTemplate.delete(Key.CODE01_PROTOCOLS);
        redisTemplate.opsForValue().set(Key.CODE01_PROTOCOLS, list);
        return R.ok(((ExcelResult<Protocol01ExcelImportVo>) map.get("listener")).getAnalysis());
    }
}
