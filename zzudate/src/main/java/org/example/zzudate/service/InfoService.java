package org.example.zzudate.service;

import java.util.List;
import org.example.zzudate.entity.Info;

public interface InfoService {
   int saveInfo(Info info);

   int updateInfo(Info info);

   int deleteInfo(String id);

   List<Info> listAll();

   List<Info> listByCategory(String category);

   List<Info> listByUserId(String userId);
}
