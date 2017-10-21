package com.migrate;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Engine;

/**
 * API引导式配置
 */
public class GlobalConfig extends JFinalConfig {
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		PropKit.use("config.properties");
		me.setDevMode(PropKit.getBoolean("devMod", false));
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
	}
	
	public void configEngine(Engine me) {
		/*me.addSharedFunction("/common/_layout.html");
		me.addSharedFunction("/common/_paginate.html");*/
	}
	
	public static DruidPlugin createDruidPlugin() {
		return new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置C3p0数据库连接池插件
		DruidPlugin druidPlugin = createDruidPlugin();
		me.add(druidPlugin);
		
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		arp.setBaseSqlTemplatePath(PathKit.getRootClassPath());
		//显示sql查询语句
		PropKit.use("config.properties");
		arp.setShowSql(PropKit.getBoolean("devMod",false));
		
		me.add(arp);
		
		//配置缓存插件
		me.add(new EhCachePlugin());
		
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
	}
	
	/**
	 * 配置处理器，用来过滤sql注入，xss等
	 */
	public void configHandler(Handlers me) {
	}

	@Override
	public void afterJFinalStart() {
		super.afterJFinalStart();
		System.out.println("Jfinal 启动了");
		//启动任务线程
		
		
		
	}

	@Override
	public void beforeJFinalStop() {
		super.beforeJFinalStop();
		
		System.out.println("Jfinal 结束了");
	}
	
}
