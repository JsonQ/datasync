package com.chris.service;


import com.chris.entity.TbSalBill4Xt;
import com.chris.entity.TbSalBillitem4Xt;
import com.chris.entity.TbSalOrder4Store;
import com.chris.entity.TbSalOrderitem4Store;

import java.util.List;

/**
 * @author 秦亮 qinliang@bigzone.com
 * @version V1.0
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date ${date} ${time}
 */
public interface DataSyncService {

    List<TbSalBill4Xt> getListTbSalBill4Xt() throws Exception;
    List<TbSalBillitem4Xt> getListTbSalBillitem4Xt() throws Exception;

    List<TbSalOrder4Store> getListTbSalOrder4Store() throws Exception;
    List<TbSalOrderitem4Store> getListTbSalOrderitem4Store() throws Exception;
    List<TbSalOrderitem4Store> getListTbSalOrderitem4StoreByBillno(TbSalOrderitem4Store salOrderitem4StoreEntity) throws Exception;
    boolean syncSaleBill2XtFromStore() throws Exception;

}
