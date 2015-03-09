package myfeed.ui;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Spencer Gibb
 */
@ConfigurationProperties("myfeed.ui")
@Data
public class UiProps {
	private String featureAaaFlag = "OFF";
}
