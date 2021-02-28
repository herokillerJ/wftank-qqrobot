package cn.wftank.qqrobot.app;


import cn.wftank.qqrobot.dao.entity.User;
import cn.wftank.qqrobot.dao.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Autowired
    private UserMapper userMapper;
    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        String sql = userQueryWrapper.select(User.class, i -> true).eq(true, "id", "1").getSqlSelect();
        System.out.println(sql);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        for (int i = 0; i < userList.size(); i++) {
            System.out.println(userList.get(i));

        }
    }
}
