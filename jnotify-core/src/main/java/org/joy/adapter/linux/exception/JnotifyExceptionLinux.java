package org.joy.adapter.linux.exception;

import org.joy.core.exception.JnotifyException;


/**
 * @author tramp
 */
public class JnotifyExceptionLinux extends JnotifyException
{
	private static final long serialVersionUID = 1L;

//	private static final int LINUX_NO_SUCH_FILE_OR_DIRECTORY = 2;
//	private static final int LINUX_PERMISSION_DENIED = 13;
//	private static final int LINUX_NO_SPACE_LEFT_ON_DEVICE = 28;

	public JnotifyExceptionLinux(String s, int systemErrorCode)
	{
		super(s, systemErrorCode);
	}

}
