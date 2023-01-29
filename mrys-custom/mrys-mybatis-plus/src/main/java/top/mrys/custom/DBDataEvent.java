package top.mrys.custom;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

/**
 * @author mrys
 */
public class DBDataEvent extends ApplicationEvent {
  public DBDataEvent(DBDataSource source) {
    super(source);
  }

  public DBDataSource getDBDataSource() {
    return (DBDataSource) getSource();
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DBDataSource {

    private TableInfo tableInfo;
    private EnumActionType type;
    private Object data;
    private Object oldData;

  }

  public static class Insert extends DBDataEvent {
    public Insert(TableInfo tableInfo, Object data) {
      super(new DBDataSource(tableInfo, EnumActionType.INSERT, data, null));
    }
  }

  public static class Update extends DBDataEvent {
    public Update(TableInfo tableInfo, Object data, Object oldData) {
      super(new DBDataSource(tableInfo, EnumActionType.UPDATE, data, oldData));
    }
  }

  public static class Delete extends DBDataEvent {
    public Delete(TableInfo tableInfo, Object data) {
      super(new DBDataSource(tableInfo, EnumActionType.DELETE, data, null));
    }
  }
}
