package org.roderick.source.lang;

/**
 * 自动异常关闭
 * ARM块使用结构：
 * try(
 *  需要捕捉异常并关闭的放这里，出现异常时，会按顺序逐个关闭
 * )
 * {
 *  逻辑操作
 * }
 * catch {
 * 	处理异常
 * }
 * 特别是在close出现异常时，会进行处理
 */
public interface AutoCloseable {
	void close() throws Exception;
}
