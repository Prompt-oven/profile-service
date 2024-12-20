package com.promptoven.profileservice.application.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.promptoven.profileservice.application.port.in.dto.FollowRequestDTO;
import com.promptoven.profileservice.application.port.in.dto.IsFollowingRequestDTO;
import com.promptoven.profileservice.application.port.in.dto.UnfollowRequestDTO;
import com.promptoven.profileservice.application.port.in.usecase.FollowingUsecase;
import com.promptoven.profileservice.application.port.out.call.FollowingPersistence;
import com.promptoven.profileservice.application.port.out.call.ProfilePersistence;
import com.promptoven.profileservice.application.service.dto.FollowingDTO;
import com.promptoven.profileservice.application.service.dto.ProfileShortDTO;
import com.promptoven.profileservice.application.service.dto.mapper.FollowingDomainDTOMapper;
import com.promptoven.profileservice.application.service.dto.mapper.ProfileDomainDTOMapper;
import com.promptoven.profileservice.domain.Following;
import com.promptoven.profileservice.domain.dto.FollowingModelDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FollowingService implements FollowingUsecase {

	private final FollowingPersistence followingPersistence;
	private final ProfilePersistence profilePersistence;
	private final FollowingDomainDTOMapper followingDomainDTOMapper;
	private final ProfileDomainDTOMapper profileDomainDTOMapper;

	@Override
	public void follow(FollowRequestDTO followRequestDTO) {
		String creatorID = profilePersistence.getProfileID(followRequestDTO.getCreatorNickname());
		FollowingModelDTO followingModelDTO = FollowingModelDTO.builder()
			.FollowerID(followRequestDTO.getFollowerID())
			.FolloweeID(creatorID)
			.build();
		Following following = Following.CreateFollowing(followingModelDTO);
		followingPersistence.follow(followingDomainDTOMapper.toDTO(following));
	}

	@Override
	public void unfollow(UnfollowRequestDTO unfollowRequestDTO) {
		String creatorID = profilePersistence.getProfileID(unfollowRequestDTO.getCreatorNickname());
		FollowingDTO followingDTO
			= followingPersistence.getFollowingObject(unfollowRequestDTO.getFollowerID(), creatorID);
		Following following = followingDomainDTOMapper.toDomain(followingDTO);
		following = Following.Unfollow(following);
		followingPersistence.unfollow(followingDomainDTOMapper.toDTO(following));
	}

	@Override
	public boolean isFollowing(IsFollowingRequestDTO isFollowingRequestDTO) {
		String follower = profilePersistence.getProfileID(isFollowingRequestDTO.getFollower());
		String followee = profilePersistence.getProfileID(isFollowingRequestDTO.getFollowee());
		return followingPersistence.isFollowing(follower, followee);
	}

	@Override
	public List<ProfileShortDTO> getFollowers(String followeeNickname) {
		String followee = profilePersistence.getProfileID(followeeNickname);
		List<String> followers = followingPersistence.getFollowers(followee);
		return followers.stream()
			.map(profilePersistence::read)
			.filter(Objects::nonNull)
			.toList().stream()
			.map(profileDomainDTOMapper::toShortDTO)
			.toList();
	}

	@Override
	public List<ProfileShortDTO> getFollowing(String followerNickname) {
		String follower = profilePersistence.getProfileID(followerNickname);
		List<String> followings = followingPersistence.getFollowings(follower);
		return followings.stream()
			.map(profilePersistence::read)
			.filter(Objects::nonNull)
			.toList().stream()
			.map(profileDomainDTOMapper::toShortDTO)
			.toList();
	}
}
