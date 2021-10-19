/*
 * Majda EL MAROUNI
 * Imane Es-sebbar
 */
package step2;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;

import org.eclipse.jdt.core.dom.PackageDeclaration;

public class PackageDeclarationVisitor extends ASTVisitor {
	int cpt=0;
	
	public boolean visit(PackageDeclaration node) {
		cpt++;
		return true;
	}
	
	public int getPackages() {
		return cpt;
	}
}
