package com.chris.service.impl;

import com.chris.dao.DataSyncDao;
import com.chris.entity.*;
import com.chris.service.DataSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author 秦亮 qinliang@bigzone.com
 * @version V1.0
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}(门店系统数据同步到协同系统)
 * @date ${date} ${time}
 */
@Service("dataSyncService")
public class DataSyncServiceImpl implements DataSyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSyncServiceImpl.class);

    @Autowired
    private DataSyncDao dataSyncDao;

    @Override
    public List<TbSalBill4Xt> getListTbSalBill4Xt() throws Exception {
        return dataSyncDao.getListTbSalBill4Xt();
    }

    @Override
    public List<TbSalBillitem4Xt> getListTbSalBillitem4Xt() throws Exception {
        return dataSyncDao.getListTbSalBillitem4Xt();
    }

    @Override
    public List<TbSalOrder4Store> getListTbSalOrder4Store() throws Exception {
        return dataSyncDao.getListTbSalOrder4Store();
    }

    @Override
    public List<TbSalOrderitem4Store> getListTbSalOrderitem4Store() throws Exception {
        return dataSyncDao.getListTbSalOrderitem4Store();
    }

    @Override
    public List<TbSalOrderitem4Store> getListTbSalOrderitem4StoreByBillno(TbSalOrderitem4Store salOrderitem4StoreEntity) throws Exception {
        return dataSyncDao.getListTbSalOrderitem4StoreByBillno(salOrderitem4StoreEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncSaleBill2XtFromStore() throws Exception {

        LOGGER.debug("syncSaleBill2XtFromStore START...");
        // 获取门店销售单列表
        List<TbSalOrder4Store> listTbSalOrder4Store = this.getListTbSalOrder4Store();
        if(listTbSalOrder4Store == null || listTbSalOrder4Store.size() == 0){
            LOGGER.debug("门店系统需要同步的销售单数量为0");
            return false;
        }
        Iterator<TbSalOrder4Store> iter = listTbSalOrder4Store.iterator();
        while(iter.hasNext()){
            TbSalOrder4Store tmpSalOrder4Store = (TbSalOrder4Store)iter.next();
            TbSalBill4Xt tmpSalBill4Xt = new TbSalBill4Xt();
            tmpSalBill4Xt.setBillNo(tmpSalOrder4Store.getBillno());
            // 检查门店系统中该笔销售单是否已存在协同系统中
            int rtnCnt = dataSyncDao.existTbSalBill4XtByBillno(tmpSalBill4Xt);
            if(rtnCnt != 0){
                // 记录存在,遍历下一条记录
                LOGGER.debug("该笔销售单在协同系统中已存在, 单号为: " + tmpSalOrder4Store.getBillno());
                continue;
            }
            this.initSalBill4Xt(tmpSalBill4Xt, tmpSalOrder4Store);
            dataSyncDao.saveTbSalBill4Xt(tmpSalBill4Xt);

            // 获取门店销售单单体
            TbSalOrderitem4Store tmpItem4Store = new TbSalOrderitem4Store();
            tmpItem4Store.setBillno(tmpSalOrder4Store.getBillno());
            List<TbSalOrderitem4Store> listTbSalOrderitem4Store = dataSyncDao.getListTbSalOrderitem4StoreByBillno(tmpItem4Store);
            if(listTbSalOrderitem4Store == null || listTbSalOrderitem4Store.size() == 0){
                throw new Exception("该笔销售单的商品明细为空, 单号为: " + tmpSalOrder4Store.getBillno());
            }
            Iterator<TbSalOrderitem4Store> iterItem = listTbSalOrderitem4Store.iterator();
            int i = 0;
            while(iterItem.hasNext()){

                TbSalOrderitem4Store tmpSalOrderitem4Store = (TbSalOrderitem4Store)iterItem.next();
                TbSalBillitem4Xt tmpSalBillitem4Xt = new TbSalBillitem4Xt();
                this.initSalBillitem4Xt(++i, tmpSalBill4Xt, tmpSalBillitem4Xt, tmpSalOrderitem4Store);
                dataSyncDao.saveTbSalBillitem4Xt(tmpSalBillitem4Xt);
            }
        }

        LOGGER.debug("syncSaleBill2XtFromStore END...");
        return true;
    }

    protected void initSalBill4Xt(TbSalBill4Xt tmpSalBill4Xt, TbSalOrder4Store tmpSalOrder4Store)throws Exception{

        TbPubEmployee tmpEmployee = new TbPubEmployee();
        tmpEmployee.setEmpname(tmpSalOrder4Store.getSalesname().trim());
//        tmpEmployee.setStorecode(tmpSalOrder4Store.getStoreno());
        tmpEmployee.setAgcycode(tmpSalOrder4Store.getDealerid());
        List<TbPubEmployee> rtnListEmployee = dataSyncDao.getListTbPubEmployee(tmpEmployee);
        if(rtnListEmployee == null || rtnListEmployee.size() == 0){
//            throw new Exception("销售单导购员信息未找到,单号: " + tmpSalBill4Xt.getBillNo());
            tmpSalBill4Xt.setSpGuide("YG000"); //导购
        }else{
            TbPubEmployee employee = rtnListEmployee.get(0);
            tmpSalBill4Xt.setSpGuide(employee.getEmpcode()); //导购
        }


        String strStatus = tmpSalOrder4Store.getStatus();
        if("Y".equals(strStatus)){
            tmpSalBill4Xt.setStatus("2");
        }else{
            tmpSalBill4Xt.setStatus("1");
        }

        tmpSalBill4Xt.setBillNo(tmpSalOrder4Store.getBillno());
        tmpSalBill4Xt.setMemo(tmpSalOrder4Store.getMemo());
        tmpSalBill4Xt.setAddress(tmpSalOrder4Store.getAddress());

        tmpSalBill4Xt.setAgcyId(tmpSalOrder4Store.getAgcyid());
        tmpSalBill4Xt.setAgcyName(tmpSalOrder4Store.getAgcyname());
        tmpSalBill4Xt.setAgcyCode(tmpSalOrder4Store.getDealerid());

        tmpSalBill4Xt.setAuditor(tmpSalOrder4Store.getAuditorname());
        tmpSalBill4Xt.setAuditTime(tmpSalOrder4Store.getAuditortime());
        tmpSalBill4Xt.setBillDate(tmpSalOrder4Store.getBilldate());
//        tmpSalBill4Xt.setBillId();
        tmpSalBill4Xt.setCreateTime(tmpSalOrder4Store.getCreatetime());
        tmpSalBill4Xt.setCreator(tmpSalOrder4Store.getCreateusername());
        tmpSalBill4Xt.setCustMobile(tmpSalOrder4Store.getTelephone());
        tmpSalBill4Xt.setCustomerName(tmpSalOrder4Store.getCustomername());

        tmpSalBill4Xt.setDealAmount(tmpSalOrder4Store.getTfinalamount());
        tmpSalBill4Xt.setDelyDate(tmpSalOrder4Store.getAppointmenttime());
        tmpSalBill4Xt.setSalesCertificate(tmpSalOrder4Store.getSalesdocno());


        tmpSalBill4Xt.setStoreId(tmpSalOrder4Store.getStoreid());

        tmpSalBill4Xt.setStoreCode(tmpSalOrder4Store.getStoreno());
        tmpSalBill4Xt.setStoreName(tmpSalOrder4Store.getStorename());
        tmpSalBill4Xt.setTdisAmount(tmpSalOrder4Store.getTdiscountamout());
        tmpSalBill4Xt.setTdisAmountRate(tmpSalOrder4Store.getSdiscountrate());
        tmpSalBill4Xt.setTotalAmount(tmpSalOrder4Store.getRetailamount());

    }

    protected void initSalBillitem4Xt(int seqno, TbSalBill4Xt tmpSalBill4Xt,
                                      TbSalBillitem4Xt tmpSalBillitem4Xt,
                                      TbSalOrderitem4Store tmpSalOrderitem4Store)throws Exception{
        tmpSalBillitem4Xt.setBillId(tmpSalBill4Xt.getBillId());
        tmpSalBillitem4Xt.setArtNo(tmpSalOrderitem4Store.getArtno());
//        tmpSalBillitem4Xt.setBillProdId();
        tmpSalBillitem4Xt.setBrandName(tmpSalOrderitem4Store.getBrandname());
        tmpSalBillitem4Xt.setSeriesName(tmpSalOrderitem4Store.getSeriesName());

//        tmpSalBillitem4Xt.setColorCode();
//        tmpSalBillitem4Xt.setColorId();
//        tmpSalBillitem4Xt.setColorName();

        tmpSalBillitem4Xt.setDisPrice(tmpSalOrderitem4Store.getFinalamount());
        tmpSalBillitem4Xt.setDisRate(tmpSalOrderitem4Store.getDiscountrate());
        tmpSalBillitem4Xt.setIsCustom(tmpSalOrderitem4Store.getIscustom());
        tmpSalBillitem4Xt.setMemo(tmpSalOrderitem4Store.getMemo());
        tmpSalBillitem4Xt.setModel(tmpSalOrderitem4Store.getModel());

        tmpSalBillitem4Xt.setProdCode(tmpSalOrderitem4Store.getProdno());
        tmpSalBillitem4Xt.setProdId(tmpSalOrderitem4Store.getProdid());
        tmpSalBillitem4Xt.setProdName(tmpSalOrderitem4Store.getProdname());

        tmpSalBillitem4Xt.setQty(tmpSalOrderitem4Store.getQuantity());

//        tmpSalBillitem4Xt.setProdImg();
        tmpSalBillitem4Xt.setRetailAmt(tmpSalOrderitem4Store.getAmount());
        tmpSalBillitem4Xt.setRetailPrice(tmpSalOrderitem4Store.getUnitprice());
        tmpSalBillitem4Xt.setSeqNo(String.valueOf(seqno));

        tmpSalBillitem4Xt.setSpec(tmpSalOrderitem4Store.getSpec());
        tmpSalBillitem4Xt.setUnitName(tmpSalOrderitem4Store.getUnitname());
    }
}


