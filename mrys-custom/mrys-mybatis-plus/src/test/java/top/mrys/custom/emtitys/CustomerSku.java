package top.mrys.custom.emtitys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author mrys
 */
@Data
@Accessors(chain = true)
public class CustomerSku extends Model<CustomerSku> {

    @TableId
    private String uuid;

    @TableField("goodsName")
    private String goodsName;
}
