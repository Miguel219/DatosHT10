import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/*Silvio Orozco 18282
 * Jose Castañeda 18161
 * HDT9
 * */
public class Main {

	private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
	
	public static void main(String[] args){

		//Se crea el scanner 
		Scanner s = new Scanner(System.in);
		//Se crea el servicio de la base de datos
		GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(new File("medicosDb/"));
		registerShutdownHook(db);
		//Variable que valida lo ingresado por el usuario
		Boolean validado = false;
		//Variable 	que almacena la opcion del menu seleccionada
		int menu = 0;
		//Se muestra el menu	
		do {
			System.out.println("Ingresa el número de una opcion del menu:\n1.Ingresar doctores\n2.Ingresar pacientes\n3.Ingresar visita"
					+ "\n4.Consultar doctores de una especialidad dada\n5.Ingresar que una persona conoce a otra"
					+ "\n6.Recomendar dada una persona especifica\n7.Recomendar dado un doctor especifico\n8.Salir");
			do{
				//Se setea validado falso
				validado = false;
				String menuStr = s.nextLine();
				try {
					menu = Integer.parseInt(menuStr);
					if((menu>=1)&&(menu<=8)) {
						validado = true;
					}else {
						System.out.println("Ingrese una opción correcta");
						validado = false;
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Ingrese una opción correcta");
					validado = false;
				}
			}while(validado==false);
			
			//Se pide un archivo para traducir y luego lo muestra traducido
			if(menu==1) {		
				Transaction tx = db.beginTx();
				try {
					//Se pide la informacion del medico
					System.out.println("Ingrese el nombre del doctor:");
					String nombre = s.nextLine();
					System.out.println("Ingrese la especialidad del doctor:");
					String especialidad = s.nextLine();
					System.out.println("Ingrese el numero del doctor:");
					String telefono = s.nextLine();
					//Se guarda en los nodos de doctores
					Node node = db.createNode(Label.label("Doctor"));
					node.setProperty("nombre", nombre);
					node.setProperty("especialidad", especialidad);
					node.setProperty("telefono", telefono);
					//Se guarda en os nodos de persona
					Node nodeP = db.createNode(Label.label("Persona"));
					nodeP.setProperty("nombre", nombre);
					nodeP.setProperty("tipo", "doctor");
					nodeP.setProperty("telefono", telefono);
					tx.success();
				} finally {
					// TODO: handle finally clause
					tx.close();
				}
			}
			if(menu==2) {		
				Transaction tx = db.beginTx();
				try {
					//Se pide la informacion del medico
					System.out.println("Ingrese el nombre del paciente:");
					String nombre = s.nextLine();
					System.out.println("Ingrese el numero del paciente:");
					String telefono = s.nextLine();
					//Se guarda en os nodos de paciente
					Node node = db.createNode(Label.label("Paciente"));
					node.setProperty("nombre", nombre);
					node.setProperty("telefono", telefono);
					//Se guarda en os nodos de persona
					Node nodeP = db.createNode(Label.label("Persona"));
					nodeP.setProperty("nombre", nombre);
					nodeP.setProperty("tipo", "paciente");
					nodeP.setProperty("telefono", telefono);
					tx.success();
				} finally {
					// TODO: handle finally clause
					tx.close();
				}
			}
			if(menu==3) {		
				Transaction tx = db.beginTx();
				try {
					//Se pide la informacion del medico
					System.out.println("Ingrese la fecha de la visita:");
					String fecha = s.nextLine();
					System.out.println("Ingrese la medicina recetada:");
					String medicina = s.nextLine();
					System.out.println("Ingrese el nombre del doctor que atendio la visita:");
					String doctor = s.nextLine();
					System.out.println("Ingrese el nombre del paciente que realizo la visita:");
					String paciente = s.nextLine();
					Result result = db.execute(
							  "MATCH (p:Paciente) WHERE p.nombre='"+ paciente +"'" + 
							  "MATCH (d:Doctor) WHERE d.nombre='"+ doctor +"'" + 
							  "CREATE (p) -[:VISITS {fecha:'"+fecha+"'}]-> (d)" +
							  "-[:PRESCRIBE]-> (m:Medicina {nombre:'"+ medicina +"'}) <-[:TAKES]- (p)" +
							  "RETURN d.nombre");
					tx.success();
				} finally {
					// TODO: handle finally clause
					tx.close();
				}
			}
			if(menu==4) {		
				Transaction tx = db.beginTx();
				try {
					//Se pide la informacion del medico
					System.out.println("Ingrese la especialidad deseada:");
					String especialidad = s.nextLine();
					Result result = db.execute(
							  "MATCH (d:Doctor) WHERE d.especialidad='"+ especialidad +"'" + 
							  "RETURN d.nombre");
					tx.success();
					System.out.println("Doctores con esa especialidad:");
					int count = 0;
					while(result.hasNext()) {
						count = count + 1;
						Map<String, Object> doctor = result.next();
						System.out.println(count + ". " + doctor.get("d.nombre") + "\n");
					};
				} finally {
					// TODO: handle finally clause
					tx.close();
				}
			}
			if(menu==5) {		
				Transaction tx = db.beginTx();
				try {
					//Se pide la informacion del medico
					System.out.println("Ingrese el nombre de una persona:");
					String persona1 = s.nextLine();
					System.out.println("Ingrese el nombre de la otra persona:");
					String persona2 = s.nextLine();
					Result result = db.execute(
							  "MATCH (p1:Persona) WHERE p1.nombre='"+ persona1 +"'" + 
							  "MATCH (p2:Persona) WHERE p2.nombre='"+ persona2 +"'" + 
							  "CREATE (p1) -[:KNOWS]-> (p2)" +
							  "RETURN p1.nombre, p2.nombre");
					tx.success();
				} finally {
					// TODO: handle finally clause
					tx.close();
				}
			}
			if(menu==6) {		
				Transaction tx = db.beginTx();
				try {
					//Se pide la informacion del medico
					System.out.println("Ingrese su nombre:");
					String nombre = s.nextLine();
					System.out.println("Ingrese la especialidad deseada:");
					String especialidad = s.nextLine();
					Result result = db.execute(
							  "MATCH (p1:Persona) -[:KNOWS]-> (p2:Persona) -[:KNOWS]-> (p:Persona)" +
							  "WHERE p1.nombre='"+ nombre +"' and p.tipo='doctor'" +
							  "MATCH (d:Doctor)" +
							  "WHERE d.nombre = p.nombre and d.especialidad = '"+ especialidad +"'" + 
							  "RETURN d.nombre, d.telefono");
					tx.success();
					System.out.println("Doctor recomendado dado que lo conoce un conocido o el conocido de un conocido:");
					while(result.hasNext()) {
						Map<String, Object> doctor = result.next();
						System.out.println("Nombre: " + doctor.get("d.nombre"));
						System.out.println("Telefono: " + doctor.get("d.telefono") + "\n");
					};
				} finally {
					// TODO: handle finally clause
					tx.close();
				}
			}
			//Sale del menu
		} while (menu!=8);
	}
}
