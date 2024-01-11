package com.tima.platform.repository.projection;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/31/23
 */
public interface NativeSql {
    String AGGREGATE_STATEMENT = "SELECT COUNT(DISTINCT name) as payee, SUM(amount) as paid," +
            " SUM(balance) as outstanding" +
            " FROM  payment_transaction_history WHERE type='TRANSFER'";

    String CONTRACT_AGGREGATE_STATEMENT = "SELECT COUNT(DISTINCT influencer_name) as payee, " +
            "(SUM(contract_amount) - SUM(balance)) as paid," +
            " SUM(balance) as outstanding" +
            " FROM  influencer_payment_contract WHERE brand_public_id = :id";

}
