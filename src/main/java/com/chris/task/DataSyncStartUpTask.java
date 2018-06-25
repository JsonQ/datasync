package com.chris.task;

import com.chris.entity.TbSalBill4Xt;
import com.chris.entity.TbSalBillitem4Xt;
import com.chris.entity.TbSalOrder4Store;
import com.chris.entity.TbSalOrderitem4Store;
import com.chris.service.DataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 秦亮 qinliang@bigzone.com
 * @version V1.0
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}(用一句话描述该文件做什么)
 * @date ${date} ${time}
 */
@Component
public class DataSyncStartUpTask implements CommandLineRunner {

    @Autowired
    private DataSyncService dataSyncService;

    @Override
    public void run(String... strings) throws Exception {
        System.out.println(">>>>>>>>>>>>> START TASK <<<<<<<<<<<<<<<<");
//        List<TbSalOrder4Store> listSalOrder4Store = dataSyncService.getListTbSalOrder4Store();
//        System.out.println(">>>>>>>>>>>>> " + listSalOrder4Store.size());
//        List<TbSalOrderitem4Store> listSalOrderitem4Store = dataSyncService.getListTbSalOrderitem4Store();
//        System.out.println(">>>>>>>>>>>>> " + listSalOrderitem4Store.size());
//        List<TbSalBill4Xt> listSalBill4Xt = dataSyncService.getListTbSalBill4Xt();
//        System.out.println(">>>>>>>>>>>>> " + listSalBill4Xt.size());
//        List<TbSalBillitem4Xt> listSalBillitem4Xt = dataSyncService.getListTbSalBillitem4Xt();
//        System.out.println(">>>>>>>>>>>>> " + listSalBillitem4Xt.size());


        dataSyncService.syncSaleBill2XtFromStore();
        System.out.println(">>>>>>>>>>>>> END TASK <<<<<<<<<<<<<<<<");
    }
}
