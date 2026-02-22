package edu.ua.programanalysis;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import sootup.core.IdentifierFactory;
import sootup.core.graph.StmtGraph;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.Body;
import sootup.core.model.SootClass;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.ClassType;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaProject;
import sootup.java.core.language.JavaLanguage;
import sootup.java.core.views.JavaView;

public class BasicSetupIfElse {
    public static void main(String[] args) {

        // 1) Load compiled bytecode from target/classes
        AnalysisInputLocation inputLocation
                = new JavaClassPathAnalysisInputLocation("target/classes");

        JavaProject project
                = JavaProject.builder(new JavaLanguage(17))
                        .addInputLocation(inputLocation)
                        .build();

        JavaView view = project.createView();

        // 2) Identify the class
        IdentifierFactory idf = project.getIdentifierFactory();
        ClassType classType = idf.getClassType("demo.TargetProgramIfElseExample"); // default package

        Optional<? extends SootClass<?>> classOpt = view.getClass(classType);
        if (classOpt.isEmpty()) {
            System.out.println("Class not found: " + classType);
            return;
        }

        SootClass<?> sootClass = classOpt.get();

        // 3) Identify the main method
        MethodSignature methodSignature
                = idf.getMethodSignature(
                        classType,
                        "maximum",
                        "int",
                        List.of("int", "int"));

        // 4) Retrieve method (either from view or from class)
        Optional<? extends SootMethod> methodOpt = view.getMethod(methodSignature);
        if (methodOpt.isEmpty()) {
            // fallback: from class using subsignature
            methodOpt = sootClass.getMethod(methodSignature.getSubSignature());
        }

        if (methodOpt.isEmpty()) {
            System.out.println("Method not found: " + methodSignature);
            return;
        }

        SootMethod sootMethod = methodOpt.get();

        //System.out.println("<== Test CFG ==>\n");
        StmtGraph<?> graph = sootMethod.getBody().getStmtGraph();

        for (Stmt stmt : graph.getNodes()) {
            //System.out.println("CFG stmt graph nodes: " + stmt);
        }

        // 5) Print Jimple body
        System.out.println("\n=== Jimple for: " + sootMethod.getSignature() + " ===");
        System.out.println(sootMethod.getBody());

        System.out.println("\nPrint Local Variables:");

        Body methodBody = sootMethod.getBody();
        Set<Local> methodLocals = methodBody.getLocals();

        for (Local local : methodLocals) {
            System.out.println(local.toString());
        }

        StmtGraph<?> stmtGraph = methodBody.getStmtGraph();
        List<Stmt> stmts = stmtGraph.getStmts();

        System.out.println("\nStmp Graph:");
        for (Stmt stmt : stmts) {
            System.out.println(stmt.toString());
        }

        System.out.println("\n=== CFG Nodes (Statements) ===");
        for (Stmt stmt : stmtGraph.getStmts()) {
            System.out.println("  " + stmt);
        }

        System.out.println("\n=== CFG Edges (Stmt -> Successors) ===");
        for (Stmt s : stmtGraph.getStmts()) {
            List<? extends Stmt> succs = stmtGraph.successors(s);
            if (succs.isEmpty()) {
                System.out.println("  " + s + "  -->  [EXIT]");
            } else {
                for (Stmt t : succs) {
                    System.out.println("  " + s + "  -->  " + t);
                }
            }
        }

    }
}
