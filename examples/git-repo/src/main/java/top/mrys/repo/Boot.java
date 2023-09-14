package top.mrys.repo;

import cn.hutool.json.JSONUtil;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mrys
 */
@Slf4j
public class Boot {

  private static final String token = "7rhVzyTtsoZCgS-5SZ7z";
  private static final Integer port = 80;
  private static final String host = "localhost";
  private static final String header_name = "PRIVATE-TOKEN";

  //  获取所有分组
  private static final String get_groups = "/api/v4/groups";

  private static final List<String> groups = List.of("qstmall");

  private static final UsernamePasswordCredentialsProvider credentialsProviderOld =
    new UsernamePasswordCredentialsProvider("yijie", "qst123456");
  private static final UsernamePasswordCredentialsProvider credentialsProviderNew =
    new UsernamePasswordCredentialsProvider("yijie", "Qst123456");


  public static void main(String[] args) {

    VertxOptions options = new VertxOptions()
      .setMaxWorkerExecuteTime(10)
      .setMaxWorkerExecuteTimeUnit(TimeUnit.HOURS)
      .setMaxEventLoopExecuteTime(10)
      .setMaxEventLoopExecuteTimeUnit(TimeUnit.HOURS);
    Vertx vertx = Vertx.vertx(options);
    WebClientOptions webClientOptions = new WebClientOptions()
      .setLogActivity(true)
      .setKeepAlive(false);
    WebClient client = WebClient.create(vertx, webClientOptions);


    同步(vertx, client);
  }

  private static void 删除group(){
  }

  private static void 同步(Vertx vertx, WebClient client) {
    client.get(port, host, get_groups)
      .ssl(false)
      .putHeader(header_name, token)
      .send()
      .map(ar -> {
        if (ar.statusCode() == 200) {
          return JSONUtil.toList(ar.bodyAsString(), Group.class);
        } else {
          throw new RuntimeException(ar.statusMessage());
        }
      })
      .compose(groups -> {
        log.info("groups:{}", groups);
        List<Future> futures = groups.stream()
          .map(group -> client
            .get(port, host, get_groups + "/" + group.getId() + "/projects?per_page=100")
            .ssl(false)
            .putHeader(header_name, token)
            .send()
            .map(ar -> {
              if (ar.statusCode() == 200) {
                List<Project> projects = JSONUtil.toList(ar.bodyAsString(), Project.class);
                projects.forEach(project -> project.setGroup(group));
                return projects;
              } else {
                throw new RuntimeException(ar.statusMessage());
              }
            })).collect(Collectors.toList());
        return CompositeFuture.all(futures)
          .flatMap(ar -> Future.succeededFuture(ar.list().stream().map(o -> (List<Project>) o)
            .collect(Collectors.toMap(o -> o.get(0).getGroup().getName(), Function.identity()))));
      }).onSuccess(projects -> {
        log.info("projects:{}", projects);
        projects.forEach((k, v) -> {
          if (groups.contains(k)) {
            log.info("group:{}", k);
            v.forEach(project -> {
              vertx.executeBlocking(promise -> {
                try {
                  log.info("project:{}", project);
                  TimeUnit.SECONDS.sleep(10);
                  String pathname = "D:\\ttgit\\" + k + "\\" + project.getName();
                  if (!Files.exists(Paths.get(pathname))) {
                    //拉取代码
                    log.info("clone:{}->{}", project.name, project.getHttp_url_to_repo());
                    Git.cloneRepository()
//                      .setBare(true)
                      .setMirror(true)
                      .setCloneAllBranches(true)
                      .setURI(project.getHttp_url_to_repo())
                      .setCredentialsProvider(credentialsProviderOld)
                      .setDirectory(new File(pathname+"\\.git"))
                      .call();
                  } else {
                    //更新代码
                    List<String> branchs = List.of("master", "test");
                    for (String branch : branchs) {
                      TimeUnit.SECONDS.sleep(10);
                      log.info("pull:{}->{}", project.name, branch);
                      Git.open(new File(pathname))
                        .pull()
                        .setCredentialsProvider(credentialsProviderOld)
                        .setRemoteBranchName(branch)
                        .setRemote("origin")
                        .call();
                    }
                  }
                  //添加远程仓库
                  List<RemoteConfig> remoteConfigs = Git.open(new File(pathname))
                    .remoteList()
                    .call();
                  if (remoteConfigs.stream()
                    .noneMatch(remoteConfig -> remoteConfig.getName().equals("new"))) {
                    log.info("add remote:{}", project.name);
                    Git.open(new File(pathname))
                      .remoteSetUrl()
                      .setRemoteName("new")
                      .setRemoteUri(new URIish("https://gitlab.qstbg.com/" + project.getGroup().getName() + "/" + project.getName() + ".git"))
                      .call();
                  }
                  //推送代码
                  log.info("push:{}->{}", project.name, project.getHttp_url_to_repo());
                  Git.open(new File(pathname))
                    .push()
                    .setPushAll()
                    .setCredentialsProvider(credentialsProviderNew)
                    .setRemote("new")
                    .call();
                  promise.complete();
                } catch (GitAPIException e) {
                  promise.fail(e);
                } catch (IOException e) {
                  promise.fail(e);
                } catch (InterruptedException e) {
                  promise.fail(e);
                  throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                  promise.fail(e);
                  throw new RuntimeException(e);
                }
              }, res -> {
                if (res.succeeded()) {
                  log.info("success");
                } else {
                  log.error("err", res.cause());
                }
              });
            });

          }
        });
      }).onFailure(err -> {
        log.error("err", err);
      });
  }

  @Data
  public static class Group {
    private int id;
    private String name;
    private String description;
  }

  @Data
  public static class Project {
    private Group group;
    private int id;
    private String name;
    private String description;

    private String http_url_to_repo;
  }
}
