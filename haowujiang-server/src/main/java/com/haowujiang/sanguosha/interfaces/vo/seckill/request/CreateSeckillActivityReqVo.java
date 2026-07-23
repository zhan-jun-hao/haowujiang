package com.haowujiang.sanguosha.interfaces.vo.seckill.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 创建秒杀活动请求对象
 */
@Data
public class CreateSeckillActivityReqVo {

    /**
     * 活动名称
     */
    @NotBlank(message = "活动名称不能为空")
    private String activityName;

    /**
     * 武将编码
     */
    @NotBlank(message = "武将编码不能为空")
    private String generalCode;

    /**
     * 秒杀总库存
     */
    @NotNull(message = "库存不能为空")
    @Min(value = 1, message = "库存至少为1")
    private Integer stock;

    /**
     * 每人限购数量
     */
    @Min(value = 1, message = "限购数量至少为1")
    private Integer limitPerUser = 1;

    /**
     * 秒杀开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须在将来")
    private LocalDateTime startTime;

    /**
     * 秒杀结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须在将来")
    private LocalDateTime endTime;
}
