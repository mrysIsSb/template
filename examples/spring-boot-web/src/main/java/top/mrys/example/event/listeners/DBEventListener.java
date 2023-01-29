package top.mrys.example.event.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import top.mrys.custom.DBDataEvent;
import top.mrys.custom.EnumActionType;

import java.util.List;
import java.util.Optional;

/**
 * @author mrys
 */
@Slf4j
@Component
public class DBEventListener implements ApplicationListener<DBDataEvent.Insert> {

  @Autowired
  private Optional<List<ConsumeDBEvent>> consumeDBEvents;

  @Override
  public void onApplicationEvent(DBDataEvent.Insert event) {
    log.info("监听到数据插入事件{}", event.getSource());
    consumeDBEvents.ifPresent(list -> list.forEach(consumeDBEvent -> {
      DBDataEvent.DBDataSource source = event.getDBDataSource();
      if (consumeDBEvent.support(source.getTableInfo().getEntityType())) {
        if (source.getType() == EnumActionType.INSERT) {
          consumeDBEvent.consumeInsert(source.getData());
        } else if (source.getType() == EnumActionType.UPDATE) {
          consumeDBEvent.consumeUpdate(source.getData(), source.getOldData());
        }
      }
    }));
  }
}
