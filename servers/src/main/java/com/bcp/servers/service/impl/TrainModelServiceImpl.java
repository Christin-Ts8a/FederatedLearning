package com.bcp.servers.service.impl;

import com.bcp.servers.mapper.TrainModelMapper;
import com.bcp.servers.model.TrainModel;
import com.bcp.servers.service.TrainModelService;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainModelServiceImpl implements TrainModelService {
    @Resource
    private TrainModelMapper trainModelMapper;

    @Override
    public TrainModel add(MultipartFile file, TrainModel trainModel) {
        TrainModel isExist = trainModelMapper.selectOne(trainModel);
        if (isExist != null) {
            return null;
        }
        trainModel.setPath("/static/model/" + file.getOriginalFilename());
        int result = trainModelMapper.insert(trainModel);
        if (result != 1) {
            return null;
        }
        String path = "";
        File upload = null;
        String realPath = "";
        FileOutputStream fos = null;
        try {
            path = ResourceUtils.getURL("classpath:").getPath();
            upload = new File(path, "/static/model");
            realPath = upload.getAbsolutePath() + file.getOriginalFilename();
            fos = new FileOutputStream(realPath);
            if (!upload.exists()) {
                upload.mkdirs();
            }
            fos.write(file.getBytes());
        }catch (IOException e) {
            e.printStackTrace();
            try {
                fos.close();
                return null;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return trainModel;
    }

    @Override
    public TrainModel update(TrainModel trainModel) {
        if (trainModelMapper.updateByPrimaryKey(trainModel) == 1) {
            return trainModelMapper.selectByPrimaryKey(trainModel);
        }
        return null;
    }

    @Override
    public List<TrainModel> list(List<TrainModel> trainModel) {
        List<TrainModel> result = new ArrayList<>();
        for (TrainModel temp : trainModel) {
            List<TrainModel> trainModels = trainModelMapper.selectByConditions(temp);
            result.addAll(trainModels);
        }
        return result;
    }

    @Override
    public boolean delete(TrainModel trainModel) {
        return trainModelMapper.deleteByPrimaryKey(trainModel) == 1;
    }
}
