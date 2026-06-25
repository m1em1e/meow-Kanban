package com.godotvillage.meowkanban.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.godotvillage.meowkanban.common.exception.BaseException;
import com.godotvillage.meowkanban.domain.entity.BoardMember;
import com.godotvillage.meowkanban.domain.entity.User;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.domain.param.UserProfileUpdateParam;
import com.godotvillage.meowkanban.domain.vo.UserInfoVO;
import com.godotvillage.meowkanban.domain.vo.UserProfileVO;
import com.godotvillage.meowkanban.mapper.BoardMemberMapper;
import com.godotvillage.meowkanban.mapper.UserMapper;
import com.godotvillage.meowkanban.mapper.UserRoleMapper;
import com.godotvillage.meowkanban.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

	@Resource
	private BoardMemberMapper boardMemberMapper;

    @Override
    public UserProfileVO getUserProfileVO(IdParam param) {
        User user = param == null || param.getId() == null
                ? getCurrentUser()
                : baseMapper.selectById(param.getId());
        if (user == null) {
            throw new BaseException("用户不存在");
        }

		UserProfileVO userProfileVO = new UserProfileVO();
		BeanUtils.copyProperties(user, userProfileVO);
		userProfileVO.setJoinedTime(user.getCreatedTime());
		return userProfileVO;
    }

    @Override
    @Transactional
    public UserProfileVO updateUserProfileVO(UserProfileUpdateParam param) {
        User user = baseMapper.selectById(param.getId());
        if (user == null) {
            throw new BaseException("用户不存在");
        }

        user.setNickname(param.getNickname().trim());
        user.setGender(param.getGender() == null ? -1 : param.getGender());
        user.setBirthday(param.getBirthday());
        if (param.getAvatarResourceId() != null) {
            user.setAvatarResourceId(param.getAvatarResourceId());
        }
        updateById(user);

		UserProfileVO userProfileVO = new UserProfileVO();
		BeanUtils.copyProperties(user, userProfileVO);
		userProfileVO.setJoinedTime(user.getCreatedTime());
		return userProfileVO;
    }

	@Override
	public List<UserInfoVO> getUserInfoList(List<Long> userIds, Long boardId) {
		List<User> users = baseMapper.selectList(Wrappers.<User>lambdaQuery()
				.in(User::getId, userIds));
		List<UserInfoVO> userInfoVOS = new ArrayList<>();
		users.forEach(user -> {
			UserInfoVO userInfoVO = new UserInfoVO();
			userInfoVO.setId(user.getId());
			userInfoVO.setNickname(user.getNickname());
			userInfoVO.setAvatarResourceId(user.getAvatarResourceId());
			String role = boardMemberMapper.selectOne(Wrappers.<BoardMember>lambdaQuery()
					.eq(BoardMember::getUserId, user.getId())
					.eq(BoardMember::getBoardId, boardId)
			).getRole();

			userInfoVO.setBoardRoleCode(role);
			userInfoVOS.add(userInfoVO);
		});
		return userInfoVOS;
	}

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        if (!StringUtils.hasText(username) || "anonymousUser".equals(username)) {
            return null;
        }

        return lambdaQuery()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0)
                .one();
    }

}
