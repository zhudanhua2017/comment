package com.zdh.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.zdh.bean.Member;
import com.zdh.bean.Orders;
import com.zdh.constant.CommentStateConst;
import com.zdh.dao.MemberDao;
import com.zdh.dao.OrdersDao;
import com.zdh.dto.OrdersDto;
import com.zdh.service.OrdersService;

@Service
public class OrdersServiceImpl implements OrdersService {

	@Resource
	private OrdersDao ordersDao;
	
	@Autowired
	private MemberDao memberDao;
	
	@Value("${businessImage.url}")
    private String businessImageUrl;

	@Override
	public boolean add(OrdersDto ordersDto) {
		Orders orders = new Orders();
		BeanUtils.copyProperties(ordersDto, orders);
		orders.setCommentState(CommentStateConst.NOT_COMMENT);
		orders.setCreateTime(new Date());
		ordersDao.insert(orders);
		return true;
	}

	@Override
	public OrdersDto getById(Long id) {
		OrdersDto result = new OrdersDto();
		Orders orders = ordersDao.selectById(id);
		BeanUtils.copyProperties(orders, result);
		return result;
	}

	@Override
	public List<OrdersDto> getListByMemberId(Long memberId) {
		List<OrdersDto> result = new ArrayList<OrdersDto>();
		Orders ordersForSelect = new Orders();
		ordersForSelect.setMemberId(memberId);
		List<Orders>  ordersList = ordersDao.select(ordersForSelect);
		for(Orders orders : ordersList) {
			OrdersDto ordersDto = new OrdersDto();
			result.add(ordersDto);
			BeanUtils.copyProperties(orders, ordersDto);
			ordersDto.setImg(businessImageUrl + orders.getBusiness().getImgFileName());
			ordersDto.setTitle(orders.getBusiness().getTitle());
			ordersDto.setCount(orders.getBusiness().getNumber());
		}
		return result;
	}

	@Override
	public List<OrdersDto> getOrdersList(OrdersDto ordersDto) {
		// TODO Auto-generated method stub
		List<OrdersDto> result = new ArrayList<>();
		List<OrdersDto> list =getListByMemberId(ordersDto.getMemberId());
		for(OrdersDto dto:list){
			OrdersDto tempDto = getById(dto.getId());
			Member member = new Member();
			Long memberId = tempDto.getMemberId();
			Long phone = memberDao.getPhoneById(memberId);
			member.setId(memberId);
			member.setPhone(phone);
			tempDto.setMember(member);
			result.add(tempDto);
		}
		
		return result;
	}

}