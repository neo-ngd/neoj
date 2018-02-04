package DNA.Core;

import DNA.*;

/**
 *  投票信息
 */
public class Vote {
    /**
     *  报名表的散列值列表
     */
    public UInt256[] enrollments;
    /**
     *  选票的数目
     */
    public Fixed8 count;
}
