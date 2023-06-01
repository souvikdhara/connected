package com.connected.services;

import com.connected.domain.Account;
import com.connected.domain.Picture;
import com.connected.domain.WallPost;
import com.connected.repositories.PictureRepository;
import com.connected.repositories.WallPostRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class WallPostService {

	private final WallPostRepository wallPostRepository;
	private final PictureRepository pictureRepository;
	private final PictureService pictureService;
	private final Sort sortDesc = Sort.by("postDateTime").descending();
	private final Sort sortAsc = Sort.by("postDateTime").ascending();

	public WallPostService(WallPostRepository wallPostRepository, PictureRepository pictureRepository,
			PictureService pictureService) {
		this.wallPostRepository = wallPostRepository;
		this.pictureRepository = pictureRepository;
		this.pictureService = pictureService;
	}

	public WallPost getWallPost(Long wallPostId) {
		return wallPostRepository.getById(wallPostId);
	}

	public void saveWallPost(WallPost wallPost) {
		wallPostRepository.save(wallPost);
	}

	@Transactional
	public void deleteWallPost(Long wallPostId, Account currentUser) {
		WallPost wallPost = wallPostRepository.getById(wallPostId);
		pictureRepository.findByParentPost(wallPost).ifPresent(picture -> {
			Picture profilePicture = currentUser.getProfilePicture();
			if (profilePicture.getId().intValue() == picture.getId().intValue()) {
				pictureService.makeProfilePicture(0L);
			}
		});
		wallPostRepository.deleteById(wallPostId);
	}

	/*
	 * Gets the last wallPosts of the current user and the users being followed by
	 * him. JPA doesn't allow database pagination on queries that use more than one
	 * JOIN FETCH, so two Queries have to be done, one to page the wallPosts that
	 * returns only the ids and one to sort and get the wallPosts using more than
	 * one JOIN FETCH.
	 */

	public List<WallPost> getHomePageWallPosts(Account currentUser) {
		List<Long> wallPostsIds = wallPostRepository.getPageHomeWallPostIds(currentUser, getPage());
		if (wallPostsIds.isEmpty()) {
			return Collections.emptyList();
		}
		return getWallPostsWithLikesAndComments(wallPostsIds, sortDesc);
	}

	public Object getAccountPicturesWallPosts(Account account) {
		List<Long> picturePostsIds = wallPostRepository.findByAuthorAndHasPicture(account, getPage());
		if (picturePostsIds.isEmpty()) {
			return Collections.emptyList();
		}
		return getWallPostsWithLikesAndComments(picturePostsIds, sortDesc);
	}

	public List<WallPost> getAccountWallPosts(Account account) {
		List<Long> accountPostsIds = wallPostRepository.findByAuthor(account, getPage());
		if (accountPostsIds.isEmpty()) {
			return Collections.emptyList();
		}
		return getWallPostsWithLikesAndComments(accountPostsIds, sortDesc);
	}

	public List<WallPost> getWallPostComments(WallPost wallPost) {
		List<Long> commentPostsIds = wallPostRepository.findByParentPost(wallPost, getPage());
		if (commentPostsIds.isEmpty()) {
			return Collections.emptyList();
		}
		return getWallPostsWithLikesAndComments(commentPostsIds, sortAsc);
	}

	public List<WallPost> getLikedWallPosts(Account account) {
		List<Long> likedPostIds = wallPostRepository.findByUserIdLikes(account.getId(), getPage());
		if (likedPostIds.isEmpty()) {
			return Collections.emptyList();
		}
		return getWallPostsWithLikesAndComments(likedPostIds, sortDesc);
	}

	/*
	 * Spring Data JPA doesn't allow to eager fetch from two bags on the same Query,
	 * so two different queries have to be done. One fetches the likes and the other
	 * the comments
	 */

	private List<WallPost> getWallPostsWithLikesAndComments(List<Long> wallPostsIds, Sort sort) {
		List<WallPost> wallPosts = wallPostRepository.getSortedWallPostsWithLikes(wallPostsIds, sort);
		return wallPostRepository.getSortedWallPostsWithComments(wallPosts, sort);
	}

	private Pageable getPage() {
		return PageRequest.of(0, 25);
	}

	@Transactional // TODO is this annotation necessary??
	public void addLikeToWallPost(Long wallPostId, Account currentUser) {
		Long currentUserId = currentUser.getId();
		if (!likeExists(wallPostId, currentUserId) && isLikeOfCurrentUser(wallPostId, currentUserId)) {
			wallPostRepository.setWallPostLike(wallPostId, currentUserId);
		}
	}

	private boolean isLikeOfCurrentUser(Long wallPostId, Long currentUserId) {
		return wallPostRepository.getOne(wallPostId).getAuthor().getId().intValue() != currentUserId.intValue();
	}

	private boolean likeExists(Long wallPostId, Long currentUserId) {
		return (wallPostRepository.checkIfLikeExists(wallPostId, currentUserId) == 1);
	}

	public WallPost getParentPost(Long parentPostId) {
		return wallPostRepository.getById(parentPostId);
	}
}
