import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.toolkits.graph.pdg.PDGNode;

public class wrapperclass extends BodyTransformer{
	
	HashMutablePDG pdg;
	HashMutablePDG pdg1;
	Map<PDGNode, List<PDGNode>> allEdges = new HashMap<PDGNode, List<PDGNode>>();
	
	Graph<String, String> di_pdg = new DirectedSparseMultigraph<String, String>();
	Graph<String, String> di_pdg1 = new DirectedSparseMultigraph<String, String>();
	int count =0;
	
	 void dependents(PDGNode pnode){
		
		List<PDGNode> dep_nodes = new ArrayList<PDGNode>();
		if(count == 0)
			dep_nodes = pdg.getDependents(pnode);
		else 
			dep_nodes = pdg1.getDependents(pnode);
		
		allEdges.put(pnode, dep_nodes);
		if(dep_nodes != null){
			for(int i=0; i<dep_nodes.size(); i++){
				int flag=0;
				for(PDGNode key : allEdges.keySet()){
		            if(dep_nodes.get(i) == key)
		            	flag=1;
				}
				if(flag == 0)
					dependents(dep_nodes.get(i));
			}
		}

	}
	
	
	@Override
	protected void internalTransform(Body b, String phase, Map options) {
		
		SootMethod sootMethod = b.getMethod();
		if(!sootMethod.isConstructor()){
			
			try{
				UnitGraph g = new EnhancedUnitGraph(b);
				
					//generatePDG cfg = new generatePDG(g);
				
				//System.out.println("Body: " + b);
				if(count == 0){
					pdg = new HashMutablePDG(g);
					System.out.println("PDG Graph : "+count+ "===========================");
					//pdg.printGraph();
					
					PDGNode startNode;
					startNode = pdg.GetStartNode();
			 		
					dependents(startNode);
					
				
					File file = new File("C:\\Users\\Mahima\\Desktop\\"+b.getMethod().getDeclaringClass().getName()+"_"+b.getMethod().getName() + ".txt");
					file.createNewFile();
						
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
			
					int n=1;
					for(PDGNode key_node : allEdges.keySet()){
						fw.append("Node "+n+ " "+key_node.getAttrib());
						fw.append("\r\n");
						n++;
					}
					
					fw.append("\r\n");
					
					n=1;
					for(PDGNode key_node : allEdges.keySet()){
						int m=1;
						
						List<PDGNode> dep = new ArrayList<PDGNode>();
						List<PDGNode> back_dep = new ArrayList<PDGNode>();
						
						for(PDGNode key_node_new : allEdges.keySet()){
							back_dep = key_node.getBackDependets();
							if(key_node.equals(key_node_new)){
								m++;
								continue;
							}
							else{
								if(pdg.dependentOn(key_node_new, key_node)){
									fw.append("Edge "+n+" "+m +" "+"Dependency Edge");
									fw.append("\r\n");
								}
								else{
									int back_dep_flag = 0;
									for(int j=0; j<back_dep.size(); j++){
										if(key_node_new == back_dep.get(j)){
											fw.append("Edge "+n+" "+m+" "+"Back-Dependency Edge");
											fw.append("\r\n");
											back_dep_flag=1;
											break;
										}	
									}
									if(back_dep_flag == 0){
										dep = allEdges.get(key_node);
										
										for(int j=0; j<dep.size(); j++){
											if(key_node_new.equals(dep.get(j))){
												fw.append("Edge "+n+" "+m+" "+"Control-Flow Edge");
												fw.append("\r\n");
											}
										}

									}
								}
							}
							m++;
						}
						n++;
					}
					
					fw.flush();
					fw.close();
					
					/*for(PDGNode key_node : allEdges.keySet()){
					di_pdg.addVertex(key_node.toShortString());
					//System.out.println("Attribute: "+key_node.getAttrib());
					}*/
					
					n=1;
					for(PDGNode key_node : allEdges.keySet()){
						di_pdg.addVertex(n+" "+key_node.getAttrib());
						n++;
					}
				
					
					/*List<PDGNode> dep = new ArrayList<PDGNode>();
					List<PDGNode> back_dep = new ArrayList<PDGNode>();*/
					int d=1, c=1, bd=1;
					/*for(PDGNode key : allEdges.keySet()){
						dep = allEdges.get(key);
						back_dep = key.getBackDependets();
						
						
						for(int i=0; i<dep.size(); i++){
							if(pdg.dependentOn(dep.get(i), key)){
								di_pdg.addEdge("Dependency Edge "+d, key.toShortString(), dep.get(i).toShortString(), EdgeType.DIRECTED);
								d++;
							}
							
							else{
								int back_dep_flag = 0;
								for(int j=0; j<back_dep.size(); j++){
									if(dep.get(i) == back_dep.get(j)){
										di_pdg.addEdge("Back-Dependency Edge "+bd, key.toShortString(), dep.get(i).toShortString(), EdgeType.DIRECTED);
										bd++;
										back_dep_flag=1;
										break;
									}	
								}
								if(back_dep_flag == 0){
									di_pdg.addEdge("Control-Flow Edge "+c, key.toShortString(), dep.get(i).toShortString(), EdgeType.DIRECTED);
									c++;
								}
							}
						}
					}*/
					
					n=1;
					for(PDGNode key_node : allEdges.keySet()){
						
						int m=1;
						
						List<PDGNode> dep = new ArrayList<PDGNode>();
						List<PDGNode> back_dep = new ArrayList<PDGNode>();
						
						for(PDGNode key_node_new : allEdges.keySet()){
							back_dep = key_node.getBackDependets();
							if(key_node.equals(key_node_new)){
								m++;
								continue;
							}
							else{
								if(pdg.dependentOn(key_node_new, key_node)){
									di_pdg.addEdge("Dependency Edge "+d, n+" "+key_node.getAttrib(), m+" "+key_node_new.getAttrib(), EdgeType.DIRECTED);
									d++;
								}
								else{
									int back_dep_flag = 0;
									for(int j=0; j<back_dep.size(); j++){
										if(key_node_new == back_dep.get(j)){
											di_pdg.addEdge("Back-Dependency Edge "+bd, n+" "+key_node.getAttrib(), m+" "+key_node_new.getAttrib(), EdgeType.DIRECTED);
											bd++;
											back_dep_flag=1;
											break;
										}	
									}
									if(back_dep_flag == 0){
										dep = allEdges.get(key_node);
										
										for(int j=0; j<dep.size(); j++){
											if(key_node_new.equals(dep.get(j))){
												di_pdg.addEdge("Control-Flow Edge "+c, n+" "+key_node.getAttrib(), m+" "+key_node_new.getAttrib(), EdgeType.DIRECTED);
												c++;
											}
										}
										
									}
								}
							}
							m++;
						}
						n++;
					}
					
					Layout<String, String> layout = new CircleLayout(di_pdg);
					layout.setSize(new Dimension(500,500));
					
					BasicVisualizationServer<String,String> vv = new BasicVisualizationServer<String,String>(layout);
					vv.setPreferredSize(new Dimension(800,800));
					vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());			
					vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
					
	
					JFrame frame = new JFrame("Graph 1");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.getContentPane().add(vv);
					frame.pack();
					frame.setVisible(true); 
					
				}

				else{
					pdg1 = new HashMutablePDG(g);
					System.out.println("PDG Graph : "+count+ "===========================");
					//pdg1.printGraph();
					
					PDGNode startNode;
					startNode = pdg1.GetStartNode();
					//System.out.println(startNode.getDependets());
			 		
					dependents(startNode);
					
					File file = new File("C:\\Users\\Mahima\\Desktop\\"+b.getMethod().getDeclaringClass().getName()+"_"+b.getMethod().getName() + ".txt");
					file.createNewFile();
						
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
			
					int n=1;
					for(PDGNode key_node : allEdges.keySet()){
						fw.append("Node "+n+ " "+key_node.getAttrib());
						fw.append("\r\n");
						n++;
					}
					
					fw.append("\r\n");
					
					n=1;
					for(PDGNode key_node : allEdges.keySet()){
						int m=1;
						
						List<PDGNode> dep = new ArrayList<PDGNode>();
						List<PDGNode> back_dep = new ArrayList<PDGNode>();
						
						for(PDGNode key_node_new : allEdges.keySet()){
							back_dep = key_node.getBackDependets();
							if(key_node.equals(key_node_new)){
								m++;
								continue;
							}
							else{
								if(pdg1.dependentOn(key_node_new, key_node)){
									fw.append("Edge "+n+" "+m+" "+"Dependency Edge");
									fw.append("\r\n");
								}
								else{
									int back_dep_flag = 0;
									for(int j=0; j<back_dep.size(); j++){
										if(key_node_new == back_dep.get(j)){
											fw.append("Edge "+n+" "+m+" "+"Back-Dependency Edge");
											fw.append("\r\n");
											back_dep_flag=1;
											break;
										}	
									}
									if(back_dep_flag == 0){
										dep = allEdges.get(key_node);
										
										for(int j=0; j<dep.size(); j++){
											if(key_node_new.equals(dep.get(j))){
												fw.append("Edge "+n+" "+m+"Control-Flow Edge");
												fw.append("\r\n");
											}
										}
										
									}
								}
							}
							m++;
						}
						n++;
					}
					
					fw.flush();
					fw.close();

					
					/*for(PDGNode key_node : allEdges.keySet()){
						di_pdg1.addVertex(key_node.toShortString());
					}*/
					
					n=1;
					for(PDGNode key_node : allEdges.keySet()){
						di_pdg1.addVertex(n+" "+key_node.getAttrib());
						n++;
					}
					
					/*List<PDGNode> dep = new ArrayList<PDGNode>();
					List<PDGNode> back_dep = new ArrayList<PDGNode>();*/
					int d=1, c=1, bd=1;
					/*for(PDGNode key : allEdges.keySet()){
						dep = allEdges.get(key);
						back_dep = key.getBackDependets();
						
						
						for(int i=0; i<dep.size(); i++){
							if(pdg1.dependentOn(dep.get(i), key)){
								di_pdg1.addEdge("Dependency Edge "+d, key.toShortString(), dep.get(i).toShortString(), EdgeType.DIRECTED);
								d++;
							}
							
							else{
								int back_dep_flag = 0;
								for(int j=0; j<back_dep.size(); j++){
									if(dep.get(i) == back_dep.get(j)){
										di_pdg1.addEdge("Back-Dependency Edge "+bd, key.toShortString(), dep.get(i).toShortString(), EdgeType.DIRECTED);
										bd++;
										back_dep_flag=1;
										break;
									}	
								}
								if(back_dep_flag == 0){
									di_pdg1.addEdge("Control-Flow Edge "+c, key.toShortString(), dep.get(i).toShortString(), EdgeType.DIRECTED);
									c++;
								}
							}
						}
						
					}*/
					
					n=1;
					for(PDGNode key_node : allEdges.keySet()){
						
						int m=1;
						
						List<PDGNode> dep = new ArrayList<PDGNode>();
						List<PDGNode> back_dep = new ArrayList<PDGNode>();
						
						for(PDGNode key_node_new : allEdges.keySet()){
							back_dep = key_node.getBackDependets();
							if(key_node.equals(key_node_new)){
								m++;
								continue;
							}
							else{
								if(pdg1.dependentOn(key_node_new, key_node)){
									di_pdg1.addEdge("Dependency Edge "+d, n+" "+key_node.getAttrib(), m+" "+key_node_new.getAttrib(), EdgeType.DIRECTED);
									d++;
								}
								else{
									int back_dep_flag = 0;
									for(int j=0; j<back_dep.size(); j++){
										if(key_node_new == back_dep.get(j)){
											di_pdg1.addEdge("Back-Dependency Edge "+bd, n+" "+key_node.getAttrib(), m+" "+key_node_new.getAttrib(), EdgeType.DIRECTED);
											bd++;
											back_dep_flag=1;
											break;
										}	
									}
									if(back_dep_flag == 0){
										dep = allEdges.get(key_node);
										
										for(int j=0; j<dep.size(); j++){
											if(key_node_new.equals(dep.get(j))){
												di_pdg1.addEdge("Control-Flow Edge "+c, n+" "+key_node.getAttrib(), m+" "+key_node_new.getAttrib(), EdgeType.DIRECTED);
												c++;
											}
										}
										
									}
								}
							}
							m++;
						}
						n++;
					}
					
					Layout<String, String> layout = new CircleLayout(di_pdg1);
					layout.setSize(new Dimension(500,500));
					
					BasicVisualizationServer<String,String> vv = new BasicVisualizationServer<String,String>(layout);
					vv.setPreferredSize(new Dimension(800,800));
					vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());			
					vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
					
	
					JFrame frame = new JFrame("Graph 2");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.getContentPane().add(vv);
					frame.pack();
					frame.setVisible(true); 
				
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			
			allEdges.clear();
			count++;
			
		}
	
			
			
			//System.out.println(pdg);
			//System.out.println(pdg.getNodes());
	
		
		
			/*CFGToDotGraph cfgToDot = new CFGToDotGraph();
			DotGraph dotGraph = cfgToDot.drawCFG(pdg, g.getBody());
			
			dotGraph.plot(b.getMethod().getName() + ".dot");*/
			
			/*PDGNode startNode;
			startNode = pdg.GetStartNode();
	 		
			dependents(startNode);
			
			Graph<String, String> di_pdg = new DirectedSparseMultigraph<String, String>();
			
			for(PDGNode key_node : allEdges.keySet()){
				di_pdg.addVertex(key_node.toShortString());
			}*/
			
			/*for(PDGNode key_node : allEdges.keySet()){
				for(PDGNode key_node1 : allEdges.keySet()){
					if(key_node == key_node1)
						continue;
					else{
						System.out.println("\n\nNode 1 : "+key_node.toShortString());
						System.out.println("\nNode 2 : "+key_node1.toShortString());
						if(pdg.dependentOn(key_node1, key_node)){
							System.out.println("\nDEPENDENT");
						}
					}
				}
				
			}*/
			
			/*List<PDGNode> dep = new ArrayList<PDGNode>();
			List<PDGNode> back_dep = new ArrayList<PDGNode>();
			int d=1, c=1, bd=1;
			for(PDGNode key : allEdges.keySet()){
				dep = allEdges.get(key);
				back_dep = key.getBackDependets();
				
				
				for(int i=0; i<dep.size(); i++){
					if(pdg.dependentOn(dep.get(i), key)){
						di_pdg.addEdge("Dependency Edge "+d, key.toShortString(), dep.get(i).toShortString(), EdgeType.DIRECTED);
						d++;
					}
					
					else{
						int back_dep_flag = 0;
						for(int j=0; j<back_dep.size(); j++){
							if(dep.get(i) == back_dep.get(j)){
								di_pdg.addEdge("Back-Dependency Edge "+bd, key.toShortString(), dep.get(i).toShortString(), EdgeType.DIRECTED);
								bd++;
								back_dep_flag=1;
								break;
							}	
						}
						if(back_dep_flag == 0){
							di_pdg.addEdge("Control-Flow Edge "+c, key.toShortString(), dep.get(i).toShortString(), EdgeType.DIRECTED);
							c++;
						}
					}
				}
			}*/
			
			//System.out.println("The graph : " + di_pdg.toString());
			
			//SimpleGraphView sgv = new SimpleGraphView();
			
			
			/*Layout<String, String> layout = new CircleLayout(di_pdg);
			layout.setSize(new Dimension(500,500));
			
			BasicVisualizationServer<String,String> vv = new BasicVisualizationServer<String,String>(layout);
			vv.setPreferredSize(new Dimension(800,800));
			vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());			
			vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
			

			JFrame frame = new JFrame("Simple Graph View");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(vv);
			frame.pack();
			frame.setVisible(true); */
			
			
			/*Layout l = new FRLayout(pdg);
			Renderer r = new PluggableRenderer();
			VisualizationViewer vv = new VisualizationViewer(l,renderer);
			JFrame jf = new JFrame();
			jf.getContentPane().add (vv);*/
			
		//DependenceGraphHelper.dotDependenceGraph(pdg, "C:\\Users\\Mahima\\Desktop\\PA", b.getMethod().getName() + ".dot", false);
		//ImageView.showImage(b.getMethod().getName() +".jpg");
		
		/*byte[] graph = pdg.toString().getBytes(Charset.forName("UTF-8"));
		try {
			FileOutputStream s = null;
			
			s= new FileOutputStream("C:\\Users\\Mahima\\Desktop\\"+ b.getMethod().getName() + ".dot");
	        s.write(graph);
	        
	        s.close();
		} catch(IOException e){
			
		}		
		
		try {
			File file = new File("C:\\Users\\Mahima\\Desktop\\file.dot");
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(pdg);
			bw.close();
		}catch (IOException e){
			e.printStackTrace();
		}*/ 
		
		
		/*System.out.println();
		System.out.println();
		//System.out.println(pdg.getDependents(m_obj2pdgNode ));
		PDGNode startNode;
		startNode = pdg.GetStartNode();
		
		
		
		ArrayList<PDGNode> allNodes = new ArrayList<PDGNode>();
		allNodes.add(startNode);
		//Map<PDGNode, List<PDGNode>> dep_mat  = new HashMap<PDGNode, List<PDGNode>>();
 		
		dependents(startNode);
		
		System.out.println("Fetching nodes and its corresponding dependent nodes: \n");
        for (Map.Entry<PDGNode, List<PDGNode>> entry : allEdges.entrySet()) {
            PDGNode key = entry.getKey();
            List<PDGNode> values = entry.getValue();
            System.out.println("Key = " + key);
            System.out.println("Values = " + values + "\n");
        }*/
		
		/*for(int i=0; i<allNodes.size(); i++){
			//System.out.println("Node " + i + ": " + allNodes.get(i));
			
			nodes = pdg.getDependents(allNodes.get(i));
			for(int j=0; j<nodes.size(); j++){
				int flag = 0;
				for(int k=0; k<allNodes.size(); k++){
					if(nodes.get(j) == allNodes.get(k))
						flag=1;
				}
				if(flag == 0)
					allNodes.add(nodes.get(j));
			}
		}
		
		
		Set<PDGNode> allNodesSet = new HashSet<PDGNode>(allNodes);
		
		System.out.println("All nodes present are: ");
		Iterator itr = allNodesSet.iterator();
		while(itr.hasNext()){
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("#####################################################################");
			System.out.println(itr.next());
		} 
		
		List<PDGNode> allNodesList = new ArrayList<PDGNode>(allNodesSet);
		
		for(int i=0; i<allNodesList.size(); i++){
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("#####################################################################");
			System.out.println(allNodesList.get(i));
		}
		
		Map<PDGNode, List<PDGNode>> allEdges = new HashMap<PDGNode, List<PDGNode>>();
		Iterator itr1 = allNodesSet.iterator();
		while(itr1.hasNext()){
			List<PDGNode> depNodes = new ArrayList<PDGNode>(pdg.getDependents(itr1.next()));
		}
		for(int i=0; i<allNodesList.size(); i++){
			List<PDGNode> depNodes = new ArrayList<PDGNode>(pdg.getDependents(allNodesList.get(i)));
			
			allEdges.put(allNodesList.get(i), depNodes);
		}
		
		System.out.println("Fetching nodes and its corresponding dependent nodes: \n");
        for (Map.Entry<PDGNode, List<PDGNode>> entry : allEdges.entrySet()) {
            PDGNode key = entry.getKey();
            List<PDGNode> values = entry.getValue();
            System.out.println("Key = " + key);
            System.out.println("Values = " + values + "\n");
        }
        
		//System.out.println("Node "+i+": "+pdg.getPostorderPDGRegionList(allNodesList.get(i)));
    	List<PDGRegion> pdgre;
		
        System.out.println("PDGNode =============================");
        for(int i=0; i<allNodesList.size(); i++){
        	//System.out.println("Node "+i+": "+pdg.getPostorderPDGRegionList(allNodesList.get(i)));
        	pdgre = new ArrayList<PDGRegion>(pdg.getPostorderPDGRegionList(allNodesList.get(i)));
		
        }
        
        for(int i=0; i<pdgre.size(); i++){
        	System.out.println("Region "+i+": "+pdgre.get(i));
        }*/
        
		
		
		//System.out.println(pdg.getDependents(PDGNode arg1));
		
		
		
		
	}
}
