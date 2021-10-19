/*
 * Majda EL MAROUNI
 * Imane Es-sebbar
 */
package step2;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClassVisitor extends ASTVisitor
{
	public static Collection<String> projectClass = new ArrayList<String>();

	private String currentPackageName;
	
	public boolean visit(PackageDeclaration node)
	{
		currentPackageName = node.getName().toString();

		return true;
	}
	
	public boolean visit(TypeDeclaration node)
	{
		String className = node.getName().toString();
		
		//projectClass.add(className);
		projectClass.add(currentPackageName + "." + className.toString());

		return true;
	}
}