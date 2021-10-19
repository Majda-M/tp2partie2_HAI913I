/*
 * Majda EL MAROUNI
 * Imane Es-sebbar
 */

package step2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.astview.*;
import org.eclipse.jdt.astview.views.*;;


public class Parser {
	
	public final static int PERCENT = 20;
	public final static int PERCENT2 = 10;
	
	public final static int X = 1;
	
	public static final String projectPath = "/home/hp/eclipse-workspace/TPtestServicesREST";
	public static final String projectSourcePath = projectPath + "/src";
	public static final String jrePath = "/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar";
	
	public static List<String> percentClassWithManyMethods = new ArrayList<String>();
	public static List<String> percentClassWithManyAttributes = new ArrayList<String>();
	
	public static Collection<String> classWithMethodsAttributes = new ArrayList<String>();
	public static Collection<String> classWithMoreThanXMethods = new ArrayList<String>();
	public static Collection<String> percentMethodsWithLargestCode = new ArrayList<String>();
	
	public static TreeSet<SetType> classWithManyMethods = new TreeSet<SetType>();
	public static TreeSet<SetType> classWithManyAttributes = new TreeSet<SetType>();
	public static TreeSet<SetType> methodsWithLargestCode = new TreeSet<SetType>();
	
	public static int maximumMethodParameter = 0;
	public static String maximumMethodParameterName = "";

	public static void main(String[] args) throws IOException {

		// read java files
		final File folder = new File(projectSourcePath);
		ArrayList<File> javaFiles = listJavaFilesForFolder(folder);
		int ClasseCpt=0,LineCpt=0,MethodCpt=0,PackagesCpt=0,MethodeLineCpt=0,AttributeCpt=0;

		//
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			// System.out.println(content);

			CompilationUnit parse = parse(content.toCharArray());
			
			//visitor1 pour compter le nombre de classes et le nombre de lignes
			TypeDeclarationVisitor visitor1 = new TypeDeclarationVisitor();
			parse.accept(visitor1);
			
			ClasseCpt+=visitor1.getTypes().size();
			for(TypeDeclaration type:visitor1.getTypes()) {
				int lineClasse = type.toString().split("\n").length;
				LineCpt+=lineClasse;
				
				//ajouter les classes qui ont un nombre precis de methodes 
				String className = getQualifiedName(type.getName().toString(),parse);
				classWithManyMethods.add(new SetType(className.toString(), type.getMethods().length));
				//ajouter les classes qui ont un nombre precis d'attribus 
				classWithManyAttributes.add(new SetType(className.toString(),type.getFields().length));
				//condition sur les classes qui ont plus de X methodes
				if (type.getMethods().length > X)
					classWithMoreThanXMethods.add(className.toString());
				
				
			}
			//visitor2 pour compter le nombre de methodes
			MethodDeclarationVisitor visitor2 =new MethodDeclarationVisitor();
			parse.accept(visitor2);
			MethodCpt+=visitor2.getMethods().size();
			
			//visitor3 pour compter le nombre de packages
			PackageDeclarationVisitor visitor3 = new PackageDeclarationVisitor();
			parse.accept(visitor3);
			PackagesCpt=visitor3.getPackages();
			
			// appel de la fontcion Function
			MethodeLineCpt+=Function(parse);
			// appel de la fonction AttributeAverage
			AttributeCpt+=AttributeAverage(parse);

		}
		
		
		//quest1 :Nombre de classes de l’application.
		System.out.println("Nombre de classes de l’application est :"+ClasseCpt);
		//quest2 :Nombre de lignes de code de l’application. 
		System.out.println("Nombre de lignes de code de l’application:"+LineCpt);
		//ques3 :Nombre total de méthodes de l’application. 
		System.out.println("Nombre total de méthodes de l’application: "+MethodCpt);
		//ques4: Nombre total de packages de l’application.
		System.out.println("Nombre total de packages de l’application: "+PackagesCpt);
		//ques5 : Nombre moyen de méthodes par classe
		System.out.println("Nombre moyen de méthodes par classe: "+ MethodCpt / ClasseCpt);
		//ques6 : Nombre moyen de lignes de code par méthode
		System.out.println("Nombre moyen de lignes de code par méthode: "+MethodeLineCpt/MethodCpt);
		//ques7 : Nombre moyen d’attributs par classe
		System.out.println("Nombre moyen d’attributs par classe: "+AttributeCpt / ClasseCpt);
		//ques8 :Les 10% des classes qui possèdent le plus grand nombre de méthodes
		percentOfClassWithManyMethods(ClasseCpt);
		System.out.println("Les "+ PERCENT +"% des classes qui possèdent le plus grand nombre de méthodes: "+percentClassWithManyMethods.toString());
		//ques9 : Les 10% des classes qui possèdent le plus grand nombre d’attributs.
		percentOfClassWithManyAttributs(ClasseCpt);
		System.out.println("Les "+ PERCENT +"% des classes qui possèdent le plus grand nombre d’attributs: "+percentClassWithManyAttributes.toString());
		//ques10:  Les classes qui font partie en même temps des deux catégories précédentes
		ClassWithManyAttributesAndMethods();
		System.out.println(" Les classes qui font partie en même temps des deux catégories précédentes: "+classWithMethodsAttributes.toString());
		//ques11: Les classes qui possèdent plus de X méthodes (la valeur de X est donnée)
		System.out.println(" Les classes qui possèdent plus de X méthodes (la valeur de X est donnée) X= "+X);
		System.out.println(classWithMoreThanXMethods.toString());
		//ques12: Les 10% des méthodes qui possèdent le plus grand nombre de lignes de code (par classe). 
		percentOfMethodsWithLargestCode(MethodCpt);
		System.out.println("Les "+PERCENT2+"% des méthodes qui possèdent le plus grand nombre de lignes de code (par classe): ");
		System.out.println(methodsWithLargestCode.toString());
		//ques13: Le nombre maximal de paramètres par rapport à toutes les méthodes de l’application. 
		System.out.println("Le nombre maximal de paramètres par rapport à toutes les méthodes de l’application: ");
		System.out.println("maximumMethodParameter : " + maximumMethodParameter + " : " + maximumMethodParameterName);
		
	}

	// read all java files from specific folder
	public static ArrayList<File> listJavaFilesForFolder(final File folder) {
		ArrayList<File> javaFiles = new ArrayList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				javaFiles.addAll(listJavaFilesForFolder(fileEntry));
			} else if (fileEntry.getName().contains(".java")) {
				// System.out.println(fileEntry.getName());
				javaFiles.add(fileEntry);
			}
		}

		return javaFiles;
	}

	// create AST
	private static CompilationUnit parse(char[] classSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		parser.setBindingsRecovery(true);
 
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
 
		parser.setUnitName("");
 
		String[] sources = { projectSourcePath }; 
		String[] classpath = {jrePath};
 
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
		parser.setSource(classSource);
		
		return (CompilationUnit) parser.createAST(null); // create and parse
	}
	
	//compter le nombre moyen de ligne par methode + trouver la methode avec plus de ligne de code +
	// trouver le nombre maximal de parametres .
	
	public static int Function(CompilationUnit parse) {
		int cpt=0;
		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {
			cpt+=method.toString().split("\n").length;
			
			//methode avec plus de ligne de code
			methodsWithLargestCode.add(new SetType(
					(method.getName() + " - " + method.getReturnType2() + " - "
							+ method.parameters()),
					cpt, method.getName().toString()));
			
			//nombre maximal de parametres 
			String className = getQualifiedName(method.getName().toString(),parse);
			if (method.parameters().size() > maximumMethodParameter)
			{
				maximumMethodParameter = method.parameters().size();
				maximumMethodParameterName = method.getName().toString() + " (" + className + ")";
			}
			
		}
		return cpt;
	}
	
	//calculer le nombre moyen des attributs 
	public static int AttributeAverage(CompilationUnit parse) {
		int cpt=0;
		TypeDeclarationVisitor visitor1 = new TypeDeclarationVisitor();
		parse.accept(visitor1);
		for(TypeDeclaration type:visitor1.getTypes()) {
			cpt+=type.getFields().length;
			
			
			
		}
		return cpt;
	}
	public static String getQualifiedName(String classNameToConvert,CompilationUnit parse)
	{
		ClassVisitor visitor1 = new ClassVisitor();
		parse.accept(visitor1);
		for(String qualifiedClassName : visitor1.projectClass)
		{
			StringBuilder a = new StringBuilder(qualifiedClassName);
			StringBuilder b = a.reverse();
			String c = a.substring(0, b.indexOf("."));
			String d = new StringBuilder(c).reverse().toString();
			
			if(d.equals(classNameToConvert))
				return qualifiedClassName;
		}
		
		return "";
	}
	//les classes qui ont plus de methodes // ici c'est 20% -pour 10% il affiche rien .
	public static void percentOfClassWithManyMethods(int classCounter)
	{
		int numberToSelect = (classCounter * PERCENT) / 100;
		int counter = 0;

		for (SetType it : classWithManyMethods)
			if (counter != numberToSelect)
			{
				percentClassWithManyMethods.add(it.getName());
				counter++;
			} else
				return;
	}
	//les classes qui ont plus d'attributs // ici c'est 20% -pour 10% il affiche rien.
	public static void percentOfClassWithManyAttributs(int classCounter)
	{
		int numberToSelect = (classCounter * PERCENT) / 100;
		int counter = 0;

		for (SetType setType : classWithManyAttributes)
			if (counter != numberToSelect)
			{
				percentClassWithManyAttributes.add(setType.getName());
				counter++;
			} else
				return;
	}
	// les classes avec plus de methodes et d'attributs
	public static void ClassWithManyAttributesAndMethods()
	{
		for (String Methods : percentClassWithManyMethods) {
			for (String Attributes : percentClassWithManyAttributes) {
				if (Methods.equals(Attributes))
					classWithMethodsAttributes.add(Methods.toString());
			}
	}
	}
	// le pourcentage de methodes avec plus de lignes de code.
	public static void percentOfMethodsWithLargestCode(int methodCounter)
	{
		int numberToSelect = (methodCounter * PERCENT2) / 100;
		int counter = 0;

		for (SetType setType : methodsWithLargestCode)
			if (counter != numberToSelect)
			{
				percentMethodsWithLargestCode.add(setType.toString());
				counter++;
			} else
				return;
	}
	public static void PrintGraphe(CompilationUnit parse) {
		TypeDeclarationVisitor visitor1 = new TypeDeclarationVisitor();
		parse.accept(visitor1);
		for(TypeDeclaration type: visitor1.getTypes()) {
			type.getMethods();
		
		}
		
	}

	// navigate method information
	public static void printMethodInfo(CompilationUnit parse) {
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		for (MethodDeclaration method : visitor.getMethods()) {
			System.out.println("Method name: " + method.getName()
					+ " Return type: " + method.getReturnType2());
		}

	}
	
	
	// navigate variables inside method
	public static void printVariableInfo(CompilationUnit parse) {

		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {

			VariableDeclarationFragmentVisitor visitor2 = new VariableDeclarationFragmentVisitor();
			method.accept(visitor2);

			for (VariableDeclarationFragment variableDeclarationFragment : visitor2
					.getVariables()) {
				System.out.println("variable name: "
						+ variableDeclarationFragment.getName()
						+ " variable Initializer: "
						+ variableDeclarationFragment.getInitializer());
			}

		}
	}
	
	
	// navigate method invocations inside method
		public static void printMethodInvocationInfo(CompilationUnit parse) {

			MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
			parse.accept(visitor1);
			for (MethodDeclaration method : visitor1.getMethods()) {

				MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
				method.accept(visitor2);

				for (MethodInvocation methodInvocation : visitor2.getMethods()) {
					System.out.println("method " + method.getName() + " invoc method "
							+ methodInvocation.getName());
				}

			}
		}

}
