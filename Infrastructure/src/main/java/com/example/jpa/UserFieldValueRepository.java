package com.example.jpa;

import com.example.dto.DataProfileResponse;
import com.example.template.Field;
import com.example.template.SourceOfData;
import com.example.template.UserFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFieldValueRepository extends JpaRepository<UserFieldValue, Integer> {

    Optional<UserFieldValue> findByUser_IdAndField_id(int userID, int fieldID);

    @Query("select u.field from UserFieldValue u where u.user.id=:userID")
    List<Field> findByUser_Id(@Param("userID") int userID);

    @Query("""
    select ufv
    from UserFieldValue ufv
    where ufv.user.id = :userId
      and ufv.field.id in (
          select f.id
          from Template t join t.fields f
          where t.id = :templateId
      )
""")
    List<UserFieldValue> findForUserAndTemplate(@Param("userId") int userId, @Param("templateId") int templateId);

    @Query("select  distinct ufv.sourceOfData from UserFieldValue ufv where ufv.user.id = :userId")
    SourceOfData[] findSourceCounts(@Param("userId") Long userId);

}
