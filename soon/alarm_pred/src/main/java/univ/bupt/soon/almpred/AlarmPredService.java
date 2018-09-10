package univ.bupt.soon.almpred;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.original.AlarmPredictionItem;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlServiceAbstract;
import org.onosproject.soon.platform.MLPlatformService;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class AlarmPredService extends ModelControlServiceAbstract {

    public AlarmPredService(MLAppType serviceName, String tableName, Class itemClass, Class platformCallbackClass,
                            DatabaseAdapter database, MLPlatformService platformService) {
        super(serviceName, tableName, itemClass, platformCallbackClass, database, platformService);
    }

    @Override
    public Pair<Set<Integer>, Set<Integer>> transAvailableDataset(int modelId) {
        // 可用的数据集有两个
        // TODO
        int websocketId = wsIds.get(modelId);
        int trainDatasetId = platformService.requestNewTrainDatasetId(websocketId);
        int testDatasetId = platformService.requestNewTestDatasetId(websocketId);
        Set<Integer> trids = Sets.newHashSet(trainDatasetId);
        Set<Integer> teids = Sets.newHashSet(testDatasetId);
        // 查询数据集
        List<Item> trainData = database.queryData("*", "",tableName,itemClass);
        int size = trainData.size();
        trainIds.put(trainDatasetId, size);
        testIds.put(testDatasetId, size);

        SegmentForDataset segmentForDataset = convertToSegmentForDataset(trainData, trainDatasetId,true);
        platformService.sendTrainData(websocketId, segmentForDataset);

        segmentForDataset.setTrainData(false);
        segmentForDataset.setDatasetId(testDatasetId);
        platformService.sendTestData(websocketId, segmentForDataset);
        return Pair.of(trids, teids);
    }

    @Override
    public List<List<Double>> parseInput(List<Item> data) {
        List<List<Double>> inputData = Lists.newArrayList();
        for (Item lpi : data) {
            AlarmPredictionItem item = (AlarmPredictionItem) lpi;
            List<Double> tmpIn = Lists.newArrayList();
            // 解析输入
            for (double d : item.getInput()) {
                tmpIn.add(d);
            }
            inputData.add(tmpIn);
        }
        return inputData;
    }

    /**
     * 将数据转换成SegmentForDataset类型的数据
     * @param data 要被转化的数据
     * @param datasetId 数据集id
     * @param isTrain 是否是训练集
     * @return 转化后的对象
     */
    private SegmentForDataset convertToSegmentForDataset(List<Item> data, int datasetId, boolean isTrain){
        SegmentForDataset rtn = new SegmentForDataset();
        rtn.setDatasetId(datasetId);
        rtn.setTrainData(isTrain);
        rtn.setPartOfDataset(false);
        rtn.setIndex(0);
        List<List<Double>> inputData = Lists.newArrayList();
        List<List<Double>> outputData = Lists.newArrayList();
        for (Item lpi : data){
            AlarmPredictionItem item = (AlarmPredictionItem) lpi;
            List<Double> tmpIn = Lists.newArrayList();
            List<Double> tmpOut = Lists.newArrayList();
            // 增加输入
            for (double i : item.getInput()){
                tmpIn.add(i);
            }
            // 增加输出
            if (item.isAlarm_happen()){
                double alarm_happen = 1.0;
                tmpOut.add(alarm_happen);
            }else {
                double alarm_happen = 0.0;
                tmpOut.add(alarm_happen);
            }
            inputData.add(tmpIn);
            outputData.add(tmpOut);
        }
        rtn.setInput(inputData);
        rtn.setOutput(outputData);
        return rtn;
    }

    /**
     * 要保证拿到的都是训练集的数据,并且要作出训练集类型的区分
     * TODO 这里没有做任何区分!!! 需要修改
     */
    @Override
    public List<Item> updateData(int offset, int limit) {
        return  database.queryData("*", " offset "+offset+" limit "+limit,
                tableName, itemClass);
    }


}
