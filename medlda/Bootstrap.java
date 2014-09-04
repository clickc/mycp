import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.StringTokenizer;


public class Bootstrap {
   public int word_dict_size=0;
   public PrintWriter log=null;
	public void mergeCorpus() throws Exception{
		FileReader fr=new FileReader(new File("bootdir/noutput.txt"));
		BufferedReader br=new BufferedReader(fr);
		FileWriter mfw=new FileWriter(new File("bootdir/newoutput.txt"));
		PrintWriter mpw=new PrintWriter(mfw);
		
		String line="";
		StringTokenizer st=null;
		String[] items;
		int itemi=0;
		Hashtable hostHash=new Hashtable();
		String host="";
		String keywords="";
		while((line=br.readLine())!=null)
		{
			mpw.println(line);
			st=new StringTokenizer(line," ");
			if(st!=null&&st.countTokens()>0)
			{
			items=new String[st.countTokens()];
			items[0]=st.nextToken();
			//itemi=0;
			//while(st.hasMoreTokens())
		//	{
			//	items[itemi]=st.nextToken();
				//itemi++;
		//	}
			
			host=items[0].trim();
		//	keywords="";
			//for(int i=1;i<items.length;i++)
			//{
				//keywords=keywords+items[i]+" ";
		//	}
		   	if(!hostHash.containsKey(host))
		   	{
		   		hostHash.put(host, "1");
		   	}	
			}
		}
		br.close();
		fr.close();
		FileReader mfr=new FileReader(new File("bootdir/moutput.txt"));
		BufferedReader mbr=new BufferedReader(mfr);
		String mline="";
		StringTokenizer mst=null;
		String[] mitems;
		int mindexi;
		String mhost="";
		String mkeywords="";
		while((mline=mbr.readLine())!=null)
		{
			mst=new StringTokenizer(mline," ");
			if(mst!=null&&mst.countTokens()>0)
			{
			mitems=new String[mst.countTokens()];
			mindexi=0;
			//while(mst.hasMoreTokens())
		//	{
		//		mitems[mindexi]=mst.nextToken();
		//		mindexi++;
		//	}
			mitems[0]=mst.nextToken();
			mhost=mitems[0].trim();
           if(!hostHash.containsKey(mhost))
            {
        	  mpw.println(mline);
            }		
		  }
		}
          mfw.close();
          mpw.close();
	  mbr.close();
	  mfr.close();
	}
	public void mergeDict() throws Exception
	{
		FileReader fr=new FileReader(new File("bootdir/dict.txt"));
		BufferedReader br=new BufferedReader(fr);
		FileWriter mfw=new FileWriter(new File("bootdir/new_dict.txt"));
		PrintWriter mpw=new PrintWriter(mfw);
		
		String line="";
		StringTokenizer st=null;
		String[] items;
		int itemi=0;
		Hashtable hostHash=new Hashtable();
		String host="";
		String keywords="";
		int maxwordindex=-1;
		int wordindex=0;
		while((line=br.readLine())!=null)
		{
			mpw.println(line);
			st=new StringTokenizer(line," ");
			if(st!=null&&st.countTokens()>1)
			{
			items=new String[st.countTokens()];
			items[0]=st.nextToken();
			items[1]=st.nextToken();
			wordindex=Integer.parseInt(items[1]);
			if(wordindex>maxwordindex)
			{
				maxwordindex=wordindex;
			}
			//itemi=0;
			//while(st.hasMoreTokens())
		//	{
			//	items[itemi]=st.nextToken();
				//itemi++;
		//	}
			
			host=items[0].trim();
		//	keywords="";
			//for(int i=1;i<items.length;i++)
			//{
				//keywords=keywords+items[i]+" ";
		//	}
		   	if(!hostHash.containsKey(host))
		   	{
		   		hostHash.put(host, "1");
		   	}	
			}
		}
		br.close();
		fr.close();
		FileReader mfr=new FileReader(new File("bootdir/old_dict.txt"));
		BufferedReader mbr=new BufferedReader(mfr);
		String mline="";
		StringTokenizer mst=null;
		String[] mitems;
		int mindexi;
		String mhost="";
		String mkeywords="";
		String newItem="";
		while((mline=mbr.readLine())!=null)
		{
			mst=new StringTokenizer(mline," ");
			if(mst!=null&&mst.countTokens()>0)
			{
			//mitems=new String[mst.countTokens()];
			//mindexi=0;
			//while(mst.hasMoreTokens())
		//	{
		//		mitems[mindexi]=mst.nextToken();
		//		mindexi++;
		//	}
			//mitems[0]=mst.nextToken();
			mhost=mline.trim();
          if(!hostHash.containsKey(mhost))
           {
        	  newItem=mhost+" "+(maxwordindex+1);
        	  mpw.println(newItem);
        	  maxwordindex++;
           }
		  }
		}
      mfw.close();
      mpw.close();
	  mbr.close();
	  mfr.close();
	}
	public void samGen() throws Exception
	{
		FileReader fr=new FileReader(new File("bootdir/new_dict.txt"));
		BufferedReader br=new BufferedReader(fr);
	   String line="";
	   StringTokenizer st=null;
	   String[] items;
	   int itemi;
	   Hashtable wordHash=new Hashtable();
	   String word="";
	   int wordindex=0;
	   while((line=br.readLine())!=null)
	   {
		 st=new StringTokenizer(line," ");
		 items=new String[st.countTokens()];
		 itemi=0;
		 while(st.hasMoreTokens())
		 {
			 items[itemi]=st.nextToken();
			 itemi++;
		 }
		 word=items[0];
		 wordindex=Integer.parseInt(items[1]);
		 if(!wordHash.containsKey(word))
		 {
			 wordHash.put(word, wordindex);
		 }
	   }	   
	   br.close();
	   fr.close();
	   FileReader samfr=new FileReader(new File("bootdir/newoutput.txt"));
	   BufferedReader sambr=new BufferedReader(samfr);
	   FileWriter samfw=new FileWriter(new File("bootdir/bootSample.txt"));
	   PrintWriter sampw=new PrintWriter(samfw);
	   String samline="";
	   StringTokenizer samst=null;
	   String[] samitems;
	   int samitemi;
	   String samhost="";
	   word_dict_size=wordHash.size();
	   int[] record=new int[wordHash.size()];
	   int tempwi=0;
	   String bootline="";
	   while((samline=sambr.readLine())!=null)
	   {
		 record=new int[wordHash.size()];
		 for(int i=0;i<record.length;i++)
		 {
			 record[i]=0;
		 }
		 samst=new StringTokenizer(samline," ");
		 samitems=new String[samst.countTokens()];
		 samitemi=0;
		 while(samst.hasMoreTokens())
		 {
			 samitems[samitemi]=samst.nextToken();
			 samitemi++;
		 }
		 samhost=samitems[0];
		 for(int i=1;i<samitems.length;i++)
		 {
			 if(wordHash.get(samitems[i])!=null)
			 {
			 tempwi=Integer.parseInt(wordHash.get(samitems[i])+"");
			 record[tempwi-1]+=1;
			 }
		 }
		 bootline="";
		 bootline=bootline+samhost+" ";
		 for(int i=0;i<record.length;i++)
		 {
			 if(record[i]>0)
			 {
			  bootline=bootline+(i+1)+":"+record[i]+" ";
			 }
		 }
		 sampw.println(bootline);
	   }
	   samfw.close();
	   sampw.close();
	   samfr.close();
	   sambr.close();
	}
	
	public void bootstrap() throws Exception
	{
		System.out.println("word_dict_size="+word_dict_size);
		FileWriter logWriter=new FileWriter(new File("bootdir/log.txt"));
		log=new PrintWriter(logWriter);
		word_dict_size=389253;
		String category="店商";
		String[] seeds;
		Hashtable seedHash=new Hashtable();
		FileReader fr=new FileReader(new File("bootdir/host.txt"));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		StringTokenizer st=null;
		String label="";
		String host="";
		while((line=br.readLine())!=null)
		{
			st=new StringTokenizer(line," ");
			if(st.countTokens()<2)
				continue;
			label=st.nextToken();
			host=st.nextToken();
			if((label.trim()).equals("店商"))
			{
				seedHash.put(host, 1);
			}
		}
		
		fr.close();
		br.close();
		
		FileReader predfr=new FileReader(new File("bootdir/mnprediction.txt"));
		BufferedReader predbr=new BufferedReader(predfr);
		Hashtable corpusHash=new Hashtable();
		while((line=predbr.readLine())!=null)
		{
			st=new StringTokenizer(line," ");
			if(st.countTokens()<2)
				continue;
			host=st.nextToken();
			label=st.nextToken();
			if((label.trim()).equals("店商")&&seedHash.get(host)==null)
			{
				corpusHash.put(host, 1);
			}
		}
		
		predbr.close();
		predfr.close();
		String[] corpus;
		
		seeds=new String[seedHash.size()];
		String[] seedHosts=new String[seedHash.size()];
		corpus=new String[corpusHash.size()];
		String[] corpusHosts=new String[corpusHash.size()];
		
		FileReader samfr=new FileReader(new File("bootdir/bootSample.txt"));
		BufferedReader sambr=new BufferedReader(samfr);
		String samValue="";
		int sami=0;
		int cori=0;
		while((line=sambr.readLine())!=null)
		{
		    st=new StringTokenizer(line," ");
		    if(st.countTokens()<2)
		    {
		    	continue;
		    }
		    host=st.nextToken();
		    if(seedHash.get(host)!=null)
		    {
		    samValue=line.replaceAll(host+" ", "");
		    seeds[sami]=samValue;
		    seedHosts[sami]=host;
		    sami++;
		    }
		    else if(corpusHash.get(host)!=null)
		    {
		    samValue=	line.replaceAll(host+" ", "");
		    corpus[cori]=samValue;
		    corpusHosts[cori]=host;
		    cori++;
		    }		    
		}
		sambr.close();
		samfr.close();
		String corpusValue="";
		String seedValue="";
		String[] items;
		int itemi;
		double maxpmi=0;	
		double[] corpusRecord=new double[word_dict_size];
		double[] seedRecord=new double[word_dict_size];
		int wordindex=0;
		double wordv=0;
		double[] p_s_pmi;
		double[] rs;
		double[] rp;
		rs=new double[seeds.length];
		for(int i=0;i<seeds.length;i++)
		{
			rs[i]=1;
		}
	
	for(int iter=0;iter<1;iter++)
	{	
	   //计算 rp	
		System.out.println("iter:"+iter);
		log.println("iter:"+iter);
		
		rp=new double[corpus.length];
		for(int i=0;i<corpus.length;i++)
		{
			rp[i]=0;
		}
		Hashtable corWordHash=new Hashtable();
		int hashword=0;
		double hashvalue=0;
		for(int i=0;i<corpus.length;i++)
		{
		//	System.out.println("corpusi="+i);
			
			corpusValue=corpus[i];
		//	corpusRecord=new double[word_dict_size];
		//	for(int d=0;d<word_dict_size;d++)
			//{
				//corpusRecord[d]=0;
			//}
			if(corpusValue==null)
			{
				continue;
			}
			st=new StringTokenizer(corpusValue," ");
			items=new String[st.countTokens()];
			itemi=0;
			corWordHash=new Hashtable();
			while(st.hasMoreTokens())
			{
				items[itemi]=st.nextToken();
				wordindex=Integer.parseInt(items[itemi].substring(0,items[itemi].indexOf(":")));
				wordv=Double.parseDouble(items[itemi].substring(items[itemi].indexOf(":")+1,items[itemi].length()));
				//corpusRecord[wordindex-1]=wordv;
				if(!corWordHash.containsKey(wordindex))
				{
					corWordHash.put(wordindex, wordv);
				}
			//	itemi++;
			}
			
           p_s_pmi=new double[seeds.length];
            for(int j=0;j<seeds.length;j++)
            {
            	p_s_pmi[j]=0;
            }
			for(int j=0;j<seeds.length;j++)
			{
				seedValue=seeds[j];
				//System.out.println("seedValue:"+seedValue);
				//seedRecord=new double[word_dict_size];
				//for(int s=0;s<word_dict_size;s++)
				//{
				//	seedRecord[s]=0;
				//}
				if(seedValue==null)
				{
					continue;
				}
				st=new StringTokenizer(seedValue," ");
				items=new String[st.countTokens()];
				itemi=0;
				while(st.hasMoreTokens())
				{
					items[itemi]=st.nextToken();
					wordindex=Integer.parseInt(items[itemi].substring(0,items[itemi].indexOf(":")));
					wordv=Double.parseDouble(items[itemi].substring(items[itemi].indexOf(":")+1,items[itemi].length()));
					//seedRecord[wordindex-1]=wordv;
					if(corWordHash.containsKey(wordindex))
					{
						hashvalue=Double.parseDouble(corWordHash.get(wordindex)+"");
						p_s_pmi[j]+=wordv*hashvalue;
						
					//	log.println("wordv="+wordv+" hashvlue="+hashvalue+" p_s_pmi:"+p_s_pmi[j]);
					//	System.out.println("wordv="+wordv+" hashvlue="+hashvalue+" p_s_pmi:"+p_s_pmi[j]);
					}
				//	itemi++;
				}
				//for(int s=0;s<word_dict_size;s++)
			//	{
				//	p_s_pmi[j]+=corpusRecord[s]*seedRecord[s];
				//}			
			}
			
			maxpmi=-1;
			for(int l=0;l<seeds.length;l++)
			{
				p_s_pmi[l]=p_s_pmi[l]+2;
				p_s_pmi[l]=Math.log(p_s_pmi[l])*Math.log(1/p_s_pmi[l]);
			}
			for(int l=0;l<seeds.length;l++)
			{
				if(p_s_pmi[l]>maxpmi)
				{
					maxpmi=p_s_pmi[l];
				}
			}
			if(maxpmi<1)
			{
			//	maxpmi=1;
			}
			for(int l=0;l<seeds.length;l++)
			{
               rp[i]+=(p_s_pmi[l]/maxpmi)*rs[l];
			}			
		}
		

		String[] oldCorpus=corpus;
		String[] oldcorpusHosts=corpusHosts;
		String[] oldSeeds=seeds;
		String[] oldseedHosts=seedHosts;
		
       int[] rpindexs=new int[rp.length];
       double[] oldrp=rp;
       for(int rpi=0;rpi<rp.length;rpi++)
       {
    	   rpindexs[rpi]=rpi;
       }
       
       sort(rp,rpindexs);
       seeds=new String[1730];
       seedHosts=new String[1730];
       for(int i=0;i<seeds.length;i++)
       {
    	   seeds[i]=oldCorpus[rpindexs[i]];
    	   seedHosts[i]=oldcorpusHosts[rpindexs[i]];
       }
       corpus=new String[oldSeeds.length+oldCorpus.length-1730];
       corpusHosts=new String[oldSeeds.length+oldCorpus.length-1730];
       int cii=0;
       for(int i=0;i<oldSeeds.length;i++)
       {
    	   corpus[cii]=oldSeeds[i];
    	   corpusHosts[cii]=oldseedHosts[i];
    	   cii++;
       }
       for(int i=seeds.length;i<oldCorpus.length;i++)
       {
    	   corpus[cii]=oldCorpus[rpindexs[i]];
    	   corpusHosts[cii]=oldcorpusHosts[rpindexs[i]];
    	   cii++;
       }
		
       rs=new double[seeds.length];
       double sumrs=0;
       for(int i=0;i<rs.length;i++)
       {
    	   rs[i]=rp[rpindexs[i]];
       } 
       for(int i=0;i<rs.length;i++)
       {
    	   sumrs+=rs[i];
       }
       if(sumrs<1)
       {
    	   sumrs=1;
       }
       for(int i=0;i<rs.length;i++)
       {
    	   rs[i]=rs[i]/sumrs;
       }
       log.println("===================iter"+iter+"==============================");
       for(int ei=0;ei<rs.length;ei++)
       {
       	System.out.println(seedHosts[ei]+" "+rs[ei]);
       	log.println(seedHosts[ei]+" "+rs[ei]);
       	
       }
	}//iter循环结束  
       
    for(int ei=0;ei<rs.length;ei++)
    {
    //	System.out.println(seedHosts[ei]+" "+rs[ei]);
    //	log.println(seedHosts[ei]+" "+rs[ei]);
    	
    }
    log.close();
}
	
	public void sort(double[] a,int[] index)
	{
		log.println("a.length:"+a.length);
		for(int l=0;l<a.length;l++)
		{
		//	log.println(l+" "+a[l]);
		}
		double maxa=-1;
		double[] olda;
		int indexa;
		int[] aindex=new int[a.length];
		int[] oldaindex;
		int al=a.length;
		for(int l=0;l<a.length;l++)
		{
			aindex[l]=l;
		}
		for(int l=0;l<al;l++)
		{
			maxa=-1;
		 for(int i=0;i<a.length;i++)
		 {
			if(a[i]>maxa)
			{
				maxa=a[i];
				index[l]=aindex[i];
			}
	      }
		 olda=a;
		 oldaindex=aindex;
		 aindex=new int[a.length-1];
		 a=new double[a.length-1];
		 indexa=0;
		 for(int k=0;k<olda.length;k++)
		 {
			//log.println("old k="+k+" "+olda[k]);
		//	System.out.println("old k="+k+" "+olda[k]+" oldaindex[k]"+oldaindex[k]+" index[l]"+index[l]);
			 if(oldaindex[k]!=index[l])
			 {
			 a[indexa]=olda[k];
			 indexa++;
			 }
		 }
		 indexa=0;
		 for(int k=0;k<oldaindex.length;k++)
		 {
			 //log.println("oldaindex k="+k+" "+oldaindex[k]);
			 if(oldaindex[k]!=index[l])
			 {
				 aindex[indexa]=oldaindex[k];
				 indexa++;
			 }
		 }
		}
				
	}
	public static void main(String[] args) throws Exception
	{
		Bootstrap boots=new Bootstrap();
		//boots.mergeCorpus();
		//boots.mergeDict();
	//boots.samGen();
	boots.bootstrap();
		/*
		double[] a={6,8,1,3,5,10,18,4};
		int[] indexs=new int[a.length];
		
		for(int i=0;i<a.length;i++)
		{
			indexs[i]=i;
		}
		boots.sort(a, indexs);
		for(int i=0;i<a.length;i++)
		{
			System.out.println(indexs[i]);
		}
		*/
	}
}
