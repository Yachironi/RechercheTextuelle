package interface_RT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.plaf.metal.MetalBorders.PaletteBorder;

 
 
 


import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import traducteur.Traducteur;

public class interfaceRT extends JFrame {

	private JFrame f;
	private JPanel pannel;
	private JTabbedPane onglets;
	private JTextArea textFr;
	private String phraseRechercher;
	private String phraseTraduit;
	private JTextArea textAng ;
	private int choix = 1;

	public  interfaceRT() {
		f = new JFrame("Recherche Textuelle");

		f.setSize(1100, 850);

		pannel = new JPanel();

		onglets = new JTabbedPane(SwingConstants.TOP);/*
													 * ici je crée un tableau
													 * d'onglets
													 */
		
		phraseRechercher =new String("text en fr");
		phraseTraduit = new String("trad en "
				+ " eng");

		textFr  = new JTextArea(phraseRechercher);// ici pour écire du
		textFr.setFont(new Font("Monaco", Font.PLAIN, 16));
		// texte en francais
		// (phrase recherché )
		 textAng = new JTextArea(phraseTraduit);// ici texte en anglais
			textAng.setFont(new Font("Monaco", Font.PLAIN, 16));
		// (phrase trouvé )
		
	}

	public void affOnglet1() {
		
		//Traducteur trad = new Traducteur("fra","eng","Files/testCorpus.txt","Files/link.txt");
		//TreeMap<String, ArrayList<String>> resultat = new TreeMap<String, ArrayList<String>> ();
		//resultat = trad.traduct("ma voiture est noire", "fra", "eng");
		
		//System.out.println(" test : treemap" + resultat );
		//for (Map.Entry<String, ArrayList<String> >entry  : resultat.entrySet()) {
			//System.out.println("Phrase traduit  : " +entry.getKey());
		 
		//}
	

		/*------------------------ onglet 1 :  Traduction  -----------------------------------*/

		
		final JPanel onglet1 = new JPanel();
		onglet1.setLayout(new BoxLayout(onglet1, BoxLayout.PAGE_AXIS));
		onglet1.setPreferredSize(new Dimension(900, 650));/*
														 * ici je crée un panel
														 * qui contiendra tout
														 * les composants de la
														 * première onglet
														 */

		JPanel panelText = new JPanel(); /*
										 * ici je crée le panel qui contiendra
										 * les panels qui afficheront la phrase
										 * recherhcer et sa traduction
										 */

		JPanel panelButton = new JPanel();/*
										 * je crée un panel qui condiendra le
										 * bouton ci dessus
										 */

		final JButton b = new JButton("Recherche Français => Anglais"); /*
																 * je crée un
																 * bouton pour
																 * traduire ou
																 * rechercher
																 * une phrase (=
																 * bouton go )
																 */
		panelButton.add(b);
		
		final Highlighter.HighlightPainter highlightPainter=new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
		  
		final Highlighter highlighter=textAng.getHighlighter();
	

		JPanel panelChoix = new JPanel();
		panelChoix.setLayout(new BoxLayout(panelChoix, BoxLayout.LINE_AXIS));
		//
		//panelChoix.add(Box.createRigidArea(new Dimension(100, 40)));
		
		final JLabel panelLangue1 = new JLabel("Français");
		panelLangue1.setFont(new Font("Cooper Black", Font.PLAIN, 30));
		panelChoix.add(panelLangue1);
		panelChoix.add(Box.createRigidArea(new Dimension(200, 40)));
		JButton choixLangue = new JButton("<= =>");
		panelChoix.add(choixLangue);
		panelChoix.add(Box.createRigidArea(new Dimension(200, 40)));
		final JLabel panelLangue2 = new JLabel("Anglais");
		panelLangue2.setFont(new Font("Cooper Black", Font.PLAIN, 30));
		panelChoix.add(panelLangue2);
		
		
		
		
		
		JScrollPane scrollPane = new JScrollPane(textFr);
		

		scrollPane.setPreferredSize(new Dimension(400, 600)); // on met le
																// JtextArea fr
																// dans un
																// scrollPane
																// afin qu'on
																// puisse se
																// déplacer

	

		JScrollPane scrollPaneAng = new JScrollPane(textAng);

		scrollPaneAng.setPreferredSize(new Dimension(400, 600));

		panelText.setLayout(new BoxLayout(panelText, BoxLayout.LINE_AXIS));

		// on ajoute jtextArea fr dans le panel contenant l'ensemble des texte (
		// phrase recherhcé et phrase trouvé )
		panelText.add(Box.createRigidArea(new Dimension(10, 10)));
		panelText.add(scrollPane);
		panelText.add(Box.createRigidArea(new Dimension(10, 10)));
		panelText.add(scrollPaneAng);
		// fin ajout

		// on ajoute le panel text et le panel bouton dans la première onglet
		onglet1.add(panelChoix);
		onglet1.add(panelText);
		onglet1.add(panelButton);

		this.onglets.addTab("Traduction", onglet1);
		// ----------fin de la première onglet -------------------------

		b.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
            	String text =textFr.getText();
            
            	textAng.setText("");
            	textAng.append(text);
            	
            	  highlighter.removeAllHighlights();
				  //  Document doc = textCompetence.getDocument();
				    try{
				      //String text = doc.getText(0, doc.getLength());
				      Matcher matcher = Pattern.compile(text).matcher(text);
				      int pos = 0;
				      while(matcher.find(pos)) {
				        int start = matcher.start();
				        int end   = matcher.end();
				        if(start>0){
				        	if((text.charAt(start-1)  ==' ') &&( text.charAt(end+1) ==' ')){
						        highlighter.addHighlight(start, end, highlightPainter);
				        	}
						        
				        }
				        
				        pos = end;
				      }
				    }catch(BadLocationException e) {
				      e.printStackTrace();
				    }
				    
				    
            	 
            }
        } );
		
		
		choixLangue.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
            	
            	panelLangue1.removeAll();
            	panelLangue2.removeAll();
            	if(choix ==1){
            	panelLangue1.setText("Anglais");
            	panelLangue1.setFont(new Font("Cooper Black", Font.PLAIN, 30));
            	
            	panelLangue2.setText("Français");
            	panelLangue2.setFont(new Font("Cooper Black", Font.PLAIN, 30));
            	b.setText("Recherche  Anglais => Français");
            	choix =0;
            	}
            	else{
            		choix =1;
            		panelLangue1.setText("Français");
                	panelLangue1.setFont(new Font("Cooper Black", Font.PLAIN, 30));
                	panelLangue2.setText("Anglais");
                	panelLangue2.setFont(new Font("Cooper Black", Font.PLAIN, 30));
                	b.setText("Recherche Français => Anglais");
            	}
            	 
            }
        } );
	}
	
	 
	
	public void affOnglet2(){
		
		 
		// ici il y aura autant de boutons que d'alphabet , quand on click sur un bouton , on va afficher tout les mots que l'user connait et afficher sa traduction
				JRadioButton bA = new JRadioButton("A");
				/*JMenuBar jMb= new JMenuBar();
				setJMenuBar(jMb);
				
				JMenu jM= new JMenu("Alphabets");
				JMenuItem jMi= new JMenuItem("A");
				jMb.add(jM);*/
				JRadioButton bB = new JRadioButton("B");
				JRadioButton bC = new JRadioButton("C");
				JRadioButton bD = new JRadioButton("D");
				JRadioButton bE = new JRadioButton("E");
				JRadioButton bF = new JRadioButton("F");
				JRadioButton bG = new JRadioButton("G");
				
				JRadioButton bH = new JRadioButton("H");
				JRadioButton bI = new JRadioButton("I");
				JRadioButton bJ = new JRadioButton("J");
				JRadioButton bK = new JRadioButton("K");
				JRadioButton bL = new JRadioButton("L");
				JRadioButton bM = new JRadioButton("M");
				JRadioButton bN = new JRadioButton("N");
				
				JRadioButton bO = new JRadioButton("O");
				JRadioButton bP = new JRadioButton("P");
				JRadioButton bQ = new JRadioButton("Q");
				JRadioButton bR = new JRadioButton("R");
				JRadioButton bS= new  JRadioButton("S");
				JRadioButton bT = new JRadioButton("T");
				JRadioButton bU = new JRadioButton("U");
				
				JRadioButton bV = new JRadioButton("V");
				JRadioButton bW = new JRadioButton("W");
				JRadioButton bX = new JRadioButton("X");
				JRadioButton bY = new JRadioButton("Y");
				JRadioButton bZ = new JRadioButton("Z");
				
				
				
				
				JPanel onglet2 = new JPanel();
				JPanel panelListButton = new JPanel();
				JPanel panelCompetance = new JPanel();
			  
				
				
				// on crée un grand grand panel :  panelCompetance qui contiendra des JscrollPanel, un pour afficher la liste des mot que l'user connait et l'autre sert à afficher un exemple lorsque la souris est sur un mot de la permière JscrollPanel (coté mot maitrisé)
				final String text=" ici il y aura tout les mots on , lorsqu'on cliqueon sur un mot , onk aura un exemple à droite";
				final JTextArea textCompetence = new JTextArea( text);
				final HashMap<String, Integer> listMot=new HashMap<String,Integer>();
				
				listMot.put("animal", 10);
				listMot.put("amour", 8);
				listMot.put("mariage", 7);
				listMot.put("clone", 4);
				listMot.put("ambesa", 9);
				listMot.put("kartable", 5);
				listMot.put("aboli", 8);
				textCompetence.setEditable(false);
				JScrollPane scrollCompetance = new JScrollPane(textCompetence);
				scrollCompetance.setPreferredSize(new Dimension(100, 200));
		       
				final JTextArea textExemple = new JTextArea(" ici il y aura des exemples ");
				JScrollPane scrollExemple = new JScrollPane(textExemple);
				scrollExemple.setPreferredSize(new Dimension(200, 200));
				scrollExemple.setSize(100,100);
				 
				scrollCompetance.setSize(20,100);
				panelCompetance.setLayout(new BoxLayout(panelCompetance, BoxLayout.LINE_AXIS));
				panelCompetance.add(Box.createRigidArea(new Dimension(10, 10)));
				panelCompetance.add(scrollCompetance);
				panelCompetance.add(Box.createRigidArea(new Dimension(10, 10)));
				panelCompetance.add(scrollExemple);
		 		
				
				
				panelListButton.setLayout(new BoxLayout(panelListButton, BoxLayout.LINE_AXIS));
				panelListButton.add(bA);
				panelListButton.add(bB);
				panelListButton.add(bC);
				panelListButton.add(bD);
				panelListButton.add(bE);
				panelListButton.add(bF);
				panelListButton.add(bG);
				
				panelListButton.add(bH);
				panelListButton.add(bI);
				panelListButton.add(bJ);
				panelListButton.add(bK);
				panelListButton.add(bL);
				panelListButton.add(bM);
				panelListButton.add(bN);
				
				panelListButton.add(bO);
				panelListButton.add(bP);
				panelListButton.add(bQ);
				panelListButton.add(bR);
				panelListButton.add(bS);
				panelListButton.add(bT);
				panelListButton.add(bU);
				
				panelListButton.add(bV);
				panelListButton.add(bW);
				panelListButton.add(bX);
				panelListButton.add(bY);
				panelListButton.add(bZ);
				final Highlighter.HighlightPainter highlightPainter=new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
				final Highlighter.HighlightPainter highlightPainter2=new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
				final String pattern ="on";
				final Highlighter highlighter=textCompetence.getHighlighter();
			
				
				onglet2.add(panelListButton);
				
				bA.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						System.out.println("fqqfq");
						/*int i=0;
						int p0=0;
						int p1=0;
						
						
						try {
							
							while(i<text.length()){
								if(text.contains(pattern)){
									p0=text.indexOf(pattern);
									System.out.println(p0);
									p1=p0+pattern.length();
									highlighter.addHighlight(p0, p1, highlightPainter);		
							
								}
								
								i++;
							}
							
							 
							
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						
						
						
						
						
						
						  
						  
						   
						   highlighter.removeAllHighlights();
						  //  Document doc = textCompetence.getDocument();
						    try{
						      //String text = doc.getText(0, doc.getLength());
						      Matcher matcher = Pattern.compile(pattern).matcher(text);
						      int pos = 0;
						      while(matcher.find(pos)) {
						        int start = matcher.start();
						        int end   = matcher.end();
						        
						        
						        
						        if(start>0){
						        	 
						        	 System.out.println( "lll _"+ text.charAt(start-1) + "_  =====  _"+ text.charAt(end)+"_"
						        			 );
						        	 
						        	if(((text.charAt(start-1)  == 32 ) ||(text.charAt(start-1)  == 39))&&( (text.charAt(end) ==32)||(text.charAt(end) ==39))){
								       System.out.println(start + " / "+ end);
						        		highlighter.addHighlight(start, end, highlightPainter);
						        	}
								        
						        }
						        pos = end;
						      }
						    }catch(BadLocationException e) {
						      e.printStackTrace();
						    }
						    
						    
						    
						    
						
						/*
						try {
							highlighter.addHighlight(2, 5, highlightPainter);
							highlighter.addHighlight(7, 9, highlightPainter2);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
						    */
						    
						  
						
						   
						   /* textCompetence.setText("");
							String tmp = "";
							for(java.util.Map.Entry<String, Integer> entry:listMot.entrySet()){
								 
								 System.out.println("mmmmmm : "+ entry.getKey());
								
								if(entry.getKey().substring(0, 1).equals("a")){
									tmp+= entry.getKey()+"\n\n" ;
									System.out.println(entry.getValue());*/
									/*if(entry.getValue()==10){
										    try{
										      
										      Matcher matcher = Pattern.compile(entry.getKey()).matcher(tmp);
										      int pos = 0;
										      while(matcher.find(pos)) {
										        int start = matcher.start();
										        int end   = matcher.end();
										       // highlighter.addHighlight(start, end, highlightPainter);
										        //highlighter.addHighlight(0,1, highlightPainter2);
										        pos = end;
										      }
										    }catch(BadLocationException e) {
										      e.printStackTrace();
										    }
										    
										
										
										 
										
									}else{
										
										
									//	 highlighter.removeAllHighlights(); 
										    try{
										       
										      Matcher matcher = Pattern.compile(entry.getKey()).matcher(tmp);
										      int pos = 0;
										      while(matcher.find(pos)) {
										        int start = matcher.start();
										        int end   = matcher.end();
										        //highlighter.addHighlight(start, end, highlightPainter2);
										       // highlighter.addHighlight(1, 3, highlightPainter);
										        pos = end;
										      }
										    }catch(BadLocationException e) {
										      e.printStackTrace();
										    }
										    
										
										
									}*/
									
									
									
									
							/*	}
								
								
							}
							
						    System.out.println("final = "+ tmp);
							textCompetence.setText(tmp);
						    
						    */
						    
						   // scroll.repaint();
						  
						
						

						
						
						/*textCompetence.setText("");
						String tmp = "";
						for(int j=0;j<listMot.size();j++){
							
							if(listMot.get(j).substring(0,1).equals("a")){
								tmp += listMot.get(j) + "\n";
								
							}
						}
						textCompetence.setText(tmp);
						*/
						/*textCompetence.setText("");
						textExemple.setText("");*/
						
						
						
					}
				} );
				
				bB.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						System.out.println("fqqfq");
						textCompetence.setText("");
						textExemple.setText("");
						
						
						
					}
				} );
			bC.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							// TODO Auto-generated method stub
							System.out.println("fqqfq");
							textCompetence.setText("");
							textExemple.setText("");
							
							
							
						}
					} );
			
			bD.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bE.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bF.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bG.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bH.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bI.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bJ.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bK.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bL.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bM.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bN.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bO.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bP.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bQ.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bR.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bS.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bT.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bU.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bV.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bW.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					
					
					
					
				}
			} );
			bX.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bY.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
			bZ.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.out.println("fqqfq");
					textCompetence.setText("");
					textExemple.setText("");
					
					
					
				}
			} );
				//panelCompetance.removeAll();
				//scrollCompetance.removeAll();
				
				onglet2.add(panelCompetance);
				onglet2.setLayout(new BoxLayout(onglet2, BoxLayout.Y_AXIS));
				onglets.addTab("Mes compétences", onglet2);
				
				
				
				

				
				
				
				
				

		
	}
	

	public void affOnglet3(){
		/*------------------------ onglet 3 :  les exo -----------------------------------*/

		JPanel onglet3 = new JPanel();
		//JPanel panelCompetence = new JPanel();
		JPanel panelExo = new JPanel();
		JTextArea textExo = new JTextArea( );
		//TODO "phrase" sera remplacer par la phrase du filtre2 dans exo.
		String phrase = new String("I am a Cat, I like to drink milk.");
		textExo.setFont(new Font("Cooper Black", Font.PLAIN, 30));
		textExo.setText(phrase);
		textExo.setEnabled(false);
		
		
		JScrollPane scrollExo = new JScrollPane(textExo);
		scrollExo.setPreferredSize(new Dimension(800, 200));
		
		 
		panelExo.add(scrollExo);
		
		
		JPanel panelChoix = new JPanel();
		JRadioButton choix1= new JRadioButton("eat");
		JRadioButton choix2= new JRadioButton("drink");
		JRadioButton choix3= new JRadioButton("drive");
		JRadioButton choix4= new JRadioButton("run");
		 
		choix1.setFont(new Font("Cooper Black", Font.PLAIN, 20));
		choix2.setFont(new Font("Cooper Black", Font.PLAIN, 20));
		choix3.setFont(new Font("Cooper Black", Font.PLAIN, 20));
		choix4.setFont(new Font("Cooper Black", Font.PLAIN, 20));
		panelChoix.add(choix1);
		panelChoix.add(choix2);
		panelChoix.add(choix3);
		panelChoix.add(choix4);
		
		JPanel panelResult=new JPanel();
		JButton buttonResultat= new JButton("Correction");
		
		panelResult.add(buttonResultat);
		

		onglet3.add(panelExo);
		onglet3.add(panelChoix);
		onglet3.add(panelResult);
		onglet3.setLayout(new BoxLayout(onglet3, BoxLayout.Y_AXIS));
		onglet3.add(Box.createRigidArea(new Dimension(400,400)));
		

		onglets.addTab("Exercices perso", onglet3);

		onglets.setOpaque(true);

		pannel.add(onglets);

		f.getContentPane().add(pannel);

		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	
	
	
	
	
	public void affInter(){
		affOnglet1();
		affOnglet2();
		affOnglet3();
	}
	public static void main(String[] args) {
		interfaceRT inter = new interfaceRT();
		inter.affInter();
		/*String d = new String("Je suis un test");
		for(int i =0; i<d.length();i++){
			System.out.println(i + " _"+ d.charAt(i)+"_");
		}*/
	}
}
