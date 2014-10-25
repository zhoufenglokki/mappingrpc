package mappingrpc.test.fastjson;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.mappingrpc.connector.test.domain.User;

public class JsonTest {

	@Test
	public void test_genericsList(){
		List<User> userList = new ArrayList<>();
		User user = new User();
		user.setId(102);
		user.setDisplayName("lokki");
		userList.add(user);
		
		user = new User();
		user.setId(101);
		user.setDisplayName("zhoufeng");
		userList.add(user);
	
		String json = JSON.toJSONString(userList, SerializerFeature.WriteClassName);
		System.err.println(json);
		
		Object obj = JSON.parse(json);
		List list = (List)obj;
		System.err.println(list.get(0).getClass());
	}
	
	@Test
	public void test_parseEmptyArray(){
		JSONArray array = JSONArray.parseArray("[]");
		System.err.println(array);
		System.err.println(array.toJSONString());
	}
	
	@Test
	public void test_parseEmptyObject(){
		JSONObject obj = JSON.parseObject("{}");
		System.err.println("obj:" + obj);
		System.err.println("toString:" + obj.toJSONString());
	}
}
