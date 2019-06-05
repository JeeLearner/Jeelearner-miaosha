package com.jeelearn.mymiaosha.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.jeelearn.mymiaosha.domain.MiaoshaGoods;
import com.jeelearn.mymiaosha.vo.GoodsVo;

@Mapper
public interface GoodsDao {

	@Select("SELECT g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date FROM goods g LEFT JOIN miaosha_goods mg ON g.id=mg.goods_id")
	public List<GoodsVo> listGoodsVo();

	@Select("SELECT g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date FROM goods g LEFT JOIN miaosha_goods mg ON g.id=mg.goods_id WHERE g.id=#{goodsId}")
	public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);

	@Update("UPDATE miaosha_goods SET stock_count = stock_count -1 WHERE goods_id=#{goodsId} and stock_count > 0")
	public int reduceStock(MiaoshaGoods g);

	@Update("update miaosha_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
	public int resetStock(MiaoshaGoods g);
}
