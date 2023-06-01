package com.connected.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connected.domain.Picture;
import com.connected.domain.WallPost;

import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {

	Optional<Picture> findByParentPost(WallPost parentPost);
}
