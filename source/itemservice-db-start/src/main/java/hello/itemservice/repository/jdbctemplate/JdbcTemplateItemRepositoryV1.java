package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JdbcTemplate
 */
@Slf4j
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {

    private final JdbcTemplate template;

    public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name, price, quantity) values(?,?,?)";
        /**
         * KeyHolder
         * 데이터를 저장할 때 PK 생성에 Identity(auto increment)방식을 사용하기 때문에
         * PK ID 값을 개발자가 지정하는 것이 아니라 데이터베이스가 생성해준다.
         * 문제는 데이터베이스가 INSERT를 완료해야 생성된 PK ID를 확인할 수 있다는 점.
         *
         * KeyHolder 와 connection.prepareStatement(sql, new String[]{"id"}) 를 사용해서
         * id를 지정해주면 INSERT 쿼리 실행 이후에 데이터베이스에서 생성된 ID 값을 조회할 수 있다.
         */
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            // 자동 증가 키
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name=?, price=?, quantity=? where id=?";
        /**
         * template.update()
         * 데이터를 변경할 때는 update() 메소드를 사용하면 된다.
         * ? 에 바인딩 할 값을 순서대로 넣어준다.
         * 반환 값은 영향 받은 로우 개수이다.
         */
        template.update(
                sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId
        );
    }

    @Override
    public Optional<Item> findById(Long id) {
        try{
            String sql = "select id, item_name, price, quantity from item where id=?";
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            // queryForObject는 값이 없을 경우 예외가 터진다.
            // 따라서 return 값 Optional.of(item)
            return Optional.of(item);
        } catch(EmptyResultDataAccessException e) {
            return Optional.empty();    // 예외 경우 empty()
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name, price, quantity from item";
//동적 쿼리
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }
        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= ?";
            param.add(maxPrice); }
        log.info("sql={}", sql);
        return template.query(sql, itemRowMapper(), param.toArray());
        // query: 결과가 하나 이상일 때
        // queryForObject: 객체 하나
    }

    private RowMapper<Item> itemRowMapper() {
        /**
         * RowMapper는 DB의 반환 결과인 ResultSet를 객체로 반환한다.
         * JdbcTemplate이 resultSet이 끝날 때까지 루프를 돌려주고,
         * 개발자는 RowMapper를 구현해서 그 내부 코드만 채운다
          */
        return ((rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        });
    }

}
