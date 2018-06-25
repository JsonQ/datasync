package com.chris.dao;

import com.chris.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author 秦亮 qinliang@bigzone.com
 * @version V1.0
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date ${date} ${time}
 */
@Mapper
public interface DataSyncDao {

    List getListTbSalBill4Xt();
    int existTbSalBill4XtByBillno(TbSalBill4Xt salBill4XtEntity);
    List getListTbSalBillitem4Xt();
    int saveTbSalBill4Xt(TbSalBill4Xt salBill4XtEntity);
    int saveTbSalBillitem4Xt(TbSalBillitem4Xt salBillitem4XtEntity);

    List getListTbSalOrder4Store();
    List getListTbSalOrderitem4Store();
    List getListTbSalOrderitem4StoreByBillno(TbSalOrderitem4Store salOrderitem4StoreEntity);

    List getListTbPubEmployee(TbPubEmployee employee);

    int updateTbSalOrder4StoreById(TbSalOrder4Store salOrder4StoreEntity);
    int updateTbSalOrderitem4StoreById(TbSalOrderitem4Store salOrderitem4StoreEntity);

}
