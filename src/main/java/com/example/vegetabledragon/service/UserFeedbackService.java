package com.example.vegetabledragon.service;

import com.example.vegetabledragon.domain.UserFeedback;
import com.example.vegetabledragon.dto.FakeNewsFeedbackRatioResponse;
import com.example.vegetabledragon.dto.FeedbackRequest;
import com.example.vegetabledragon.dto.UserFeedbackResponse;
import com.example.vegetabledragon.exception.PostNotFoundException;
import com.example.vegetabledragon.exception.UserNotFoundException;

import java.util.List;
import java.util.Map;

public interface UserFeedbackService {
    // 피드백 저장
    UserFeedbackResponse saveFeedback(Long postId, String username, FeedbackRequest request);

    // 특정 게시글의 피드백 조회
    List<UserFeedbackResponse> getFeedbacksByPost(Long postId);

    // 특정 게시글의 가짜뉴스 비율 조회
    FakeNewsFeedbackRatioResponse getFakeNewsFeedbackRatio(Long postId);

}
