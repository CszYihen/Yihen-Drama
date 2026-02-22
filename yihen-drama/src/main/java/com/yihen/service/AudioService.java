package com.yihen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihen.entity.Storyboard;

import java.util.List;

public interface AudioService extends IService<Storyboard> {

    List<Storyboard> getStoryboardsByEpisodeId(Long episodeId);
}
