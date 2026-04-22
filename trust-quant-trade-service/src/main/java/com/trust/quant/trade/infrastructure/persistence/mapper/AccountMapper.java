package com.trust.quant.trade.infrastructure.persistence.mapper;

import com.trust.quant.trade.infrastructure.persistence.model.AccountEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface AccountMapper {

    @Select("""
            SELECT id, account_id, available_balance, update_time
            FROM qt_account
            WHERE account_id = #{accountId}
            """)
    AccountEntity findByAccountId(@Param("accountId") String accountId);

    @Insert("""
            INSERT INTO qt_account (id, account_id, available_balance, update_time)
            VALUES (#{id}, #{accountId}, #{availableBalance}, #{updateTime})
            """)
    int insert(AccountEntity accountEntity);

    @Update("""
            UPDATE qt_account
            SET available_balance = #{balance}, update_time = #{updateTime}
            WHERE account_id = #{accountId}
            """)
    int updateBalance(@Param("accountId") String accountId,
                      @Param("balance") BigDecimal balance,
                      @Param("updateTime") LocalDateTime updateTime);
}
