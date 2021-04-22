There are 3 steps that are executed in the backend in order to obtain the enriched information in the mails and
employees indexes.

- profiles_enrichment_processor.py
- mail_enrichment_processor.py
- mail_role_detection_taxo.py

Profile enrichment scripts are using the following steps:

- the data is being read from the azure sql database
- for the content,skills,about_me,topics the following steps are being used:
    - the content is being tokenized, and the stopwords are being removed
    - from the tokenized words we build: bigrams,trigrams, and we extract the lemmas
    - we iterate through our list of words:
        - for each domain_enrichment we check for the term presence in that domain vocabulary
        - if the term is being found, the associated words are being retrieved
        - for the corresponding domain we collect the associated words

The same steps are being used for the mail processing.

The output of the processing pipeline will contain the original fields plus associated de_ _domain_ fields.



------------

**Example Processing pipeline**

```
Input - about_me field:

"I'm a Development Lead with 12 years of experience (4 in this role). I use Excel and Adobe. I have a Bachelors Degree in Civil Engineering from the University of Washington. I am married with a daughter (8) and 2 sons (2 and 9). I have a cat. I go snow-boarding, I like to work out at the gym, and I play basketball with my friends. I like Rap music. I love movies (especially SciFi, Horror, and Disaster). . Development Lead"


Output - about_me field:
"development,lead,experience,years,experience,role,excel,adobe,bachelor,degree,bachelor degree,university,civil engineering, civil,engineering..."

Input - skills field:
"microsoft visual studio 2017,project management,deep learning,service bus,python,hadoop,content management,spark structured streaming"

Output - skills field:
"microsoft visual studio, visual studio, project management,project,management,deep learning,service bus,python,hadoop,content,content management,spark,spark structured streaming"

Input - responsibilities field:
"rapidsai,analyst,chemistry,nvidia jetson,architecture,image segmentation,functional analysis,flow"

Output - responsibilities field:
"rapidsai,analyst,chemistry,nvidia,nvidia jetson,architecture,image,segmentation,image segmentation,functional,analysis,functional analysis,flow"
```

Once the keywords from the input text are extracted, we feed them to our skills enrichment models.

TODO: review the list of the taxonomy
(For the moment we have several profile enrichment models for different domains: Software Engineering, Data
Science,Legal,Finance,Facilities,Human Resources,Sales and Marketing)

The output will look like the following:

```
SOFTWARE ENGINEERING:
lead : analytic-functions,analytical,correlated-subquery,helpdesk,scalar-subquery
experience : internship,hiring,motivation,programmer-skills,professional-experience
role : roles,user-roles,security-roles,acl,role-based-access-control
excel : vba,excel-vba,workbook,excel-external-data,pivot-chart
adobe : adobe-flash-cs,creative-cloud,flash,flash-cs,macromedia
degree : masters-degree,academia,university,internship,developer-skills
engineering : masters-degree,circuit-diagram,innovation,circuit,exercise
university : degree,academia,masters-degree,professional-experience,internship
cat : bash,shell,awk,grep,unix
go : revel,rust,gob,gorilla,goroutine
snow : rparallel,doparallel,domc,snowfall,parallel-foreach
like : facebook-like,facepile,facebook-likebox,sql-like,fan-page
friends : facebook-requests,fan-page,fql,facebook-group,facebook-stream-story
music : midi-instrument,pitch-tracking,music-player,artwork,sound-synthesis
movies : movie,video,youtube,ripping,tv
year : leap-second,mktime,unix-timestamp,strtotime,relative-date
friend : friend-class,friend-function,friendship,member,non-member-functions
movie : video,movies,avi,quicktime,finalcut
microsoft : mcpd,dreamspark,non-programming,ms-office,office
visual : xlw,visual-c++,visual-studio,visual-studio-express,natvis
project : projects,projects-and-solutions,project-management,solution,ide
management : managment,recruiting,it,people,outsourcing
deep : shallow-copy,deep-copy,lua-table,contain,object
service : windows-services,web-services,foreground-service,bindservice,pendingintent
bus : processor,amba,soc,chipset,pentium
python : numpy,six,cstringio,python-importlib,numexpr
hadoop : apache-spark,apache-tez,cloudera,mapreduce,hdfs
content : page,pages,article,featured,weebly
spark : spark-skinning,flex-spark,flex-mx,itemrenderer,viewstack
streaming : stream,video-streaming,live-streaming,http-streaming,audio-streaming
visual studio : visual-c++,visual-studio-express,visual-studio-project,vsinstaller,vsprops
project management : development-process,teamwork,project-management-tools,cmmi,agile-project-management
deep learning : forecast,prediction,deep-learning,computer-vision,neural-network,artificial-intelligence,ml,data-mining,data-analysis,statistics,machine-learning,data-science,dbn,softmax,conv-neural-network,imagenet,cross-entropy
service bus : brokeredmessage,azureservicebus,azure-servicebus-queues,azure-servicebus-topics,azure-pack
content management : content-management-system,cms,content-repository,wcm,ecm
visual studio project : visual-studio,visual-studio-shell,vsprops,copy-local,visual-studio-templates
chemistry : cheminformatics,math,orbital-mechanics,curvesmoothing,diophantine
nvidia : ati,video-card,sli,gpu,tesla
architecture : architectural-patterns,design,design-patterns,n-tier-architecture,domain-driven-design
image : image-resizing,crop,image-quality,png,photo
segmentation : adaptive-threshold,haar-wavelet,pgm,mathematical-morphology,image-processing
analysis : data-mining,statistics,churn,data-analysis,complexity
flow : network-flow,edmonds-karp,ford-fulkerson,independent-set,minimum-cut
image segmentation : watershed,mathematical-morphology,image-morphology,image-processing,binary-image

DATA SCIENCE:
degree : masters-degree,academia,university,internship,developer-skills,training-courses,recruiting,motivation,hiring,it,professional-experience,study,career-development,programmer-skills,computer-science-theory,self-improvement,competitions,experience,knowledge,non-technical
university : degree,academia,masters-degree,professional-experience,internship,it,recruiting,motivation,hiring,knowledge,developer-skills,project-ideas,technology,training-courses,self-improvement,programmer-skills,experience,study,non-technical,workplace
snow : rparallel,doparallel,domc,snowfall,parallel-foreach,mclapply,r-bigmemory,microsoft-r,blotter,deployr,r-raster,rstan,purrr,quantstrat,mapply,revolution-r,spmd,r-caret,glmnet,r-mice
python : numpy,six,cstringio,python-importlib,numexpr,pypm,sys.path,python-import,pythonxy,wing-ide,python-multiprocessing,pickle,ipython,python-unicode,python-multithreading,pandas,python-datetime,python-module,pythonw,matplotlib
hadoop : apache-spark,apache-tez,cloudera,mapreduce,hdfs,cloudera-sentry,apache-whirr,hama,tachyon,hbasestorage,bigdata,hadoop-partitioning,hadoop-lzo,metastore,mapr,hortonworks,distcp,pig,apache-spark-standalone,hadoop-plugins
deep learning : dbn,softmax,conv-neural-network,imagenet,cross-entropy,autoencoder,keras-layer,activation-function,pycaffe,word-embedding,pylearn,rbm,feed-forward,lasagne,mnist,nolearn,cntk,deeplearning4j,deconvolution,keras,data-science,data-science,machine-learning,machine-learning,statistics,statistics,data-analysis,data-analysis,data-mining,data-mining,ml,ml,artificial-intelligence,artificial-intelligence,neural-network,neural-network,computer-vision,computer-vision,deep-learning,deep-learning,prediction,prediction,forecast,forecast
image segmentation : watershed,mathematical-morphology,image-morphology,image-processing,binary-image,iris-recognition,connected-components,edge-detection,fuzzy-c-means,mean-shift,adaptive-threshold,image-enhancement,computer-vision,mahotas,image-registration,gabor-filter,mser,canny-operator,lbph-algorithm,skimage

LEGAL:
experience:legal,advice,planning,manager,accounting;
service:tax,matters,senior,income,clients


HR:
development:health,company,professional,human,talent management;
experience:health,company,professional,human,talent management;
engineering:company,health,management,area,talent management;
university:health,management,human resources,company,human;
gym:lifestyle,management,company,manager,human;
management:health,company,professional,manager,human;
service:talent management,human resources,company,performance,experience;
analysis:company,management,experience,wellness,health

FACILITIES:
development:project,business,team,sector,company;
experience:process,team,leadership,business,quality;
engineering:process,area,management,quality,company;
project:development,area,projects,general,company;
management:design,leadership,area,engineering,projects
```

**Assumptions**: *the extracted associated words can be used as indicators for a possible person skill.*


------------

Role detection pipeline is making use of a custom model. For each user present in the system, all its mails are being
aggregated.

For each mail belonging to a user:

- the mail is being split in fragments.
- for each fragment a role (and an associated score) is being assigned to that fragment
- once the roles were collected for the mail content:
- we extract the most common role,the role with the highest score
- all this information is computed again after processing all the mails for the given user
    

