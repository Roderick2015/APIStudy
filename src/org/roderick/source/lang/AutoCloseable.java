package org.roderick.source.lang;

/**
 * �Զ��쳣�ر�
 * ARM��ʹ�ýṹ��
 * try(
 *  ��Ҫ��׽�쳣���رյķ���������쳣ʱ���ᰴ˳������ر�
 * )
 * {
 *  �߼�����
 * }
 * catch {
 * 	�����쳣
 * }
 * �ر�����close�����쳣ʱ������д���
 */
public interface AutoCloseable {
	void close() throws Exception;
}
