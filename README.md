S3F
===

S3F (Scalable Systems and Simulations Framework) é um framework open source, gratuito para a criação de ambientes de desenvolvimento e simuladores em java, com carregamento dinâmico de plugins.


**Funcionalidades:**

- Carrega plugins automaticamente na inicialização
- Suporte a dependências entre plugins
- Criação da interface do usuário dinamicamente
- Abstração ao ler e salvar elementos do projeto
- Editor de texto com suporte à destaque de sintaxe
- Console para depuração
- Gerenciador de sistemas e simuladores
- Suporte integrado à internacionalização por plugin

JIFI
----

S3F foi desenvolvido para permitir a criação de plugins para a segunda versão do [JIFI], Java Interactive Flowchart Interpreter, projeto do [peteco], grupo [PET] de engenharia de computação da [UTFPR], que tem como o objetivo a criação de uma ferramenta didática para o ensino de programação e robótica usando a plataforma [Arduino].

**Gif versão alfa (S3F + Magenta + JIFI + DWRS):**

![gif](https://github.com/anderson-/S3F/raw/master/alfa.gif "S3F+Magenta+JIFI+DWRS versão alfa")

Quero Contribuir!
-----------------

O script em shell (Linux, precisa dos comandos ant e git) abaixo automatiza o download dos projetos necessários para começar a desenvolver seus próprios plugins para S3F, ele realiza as seguintes operações:

- Baixa o repositório git do S3F
- Baixa o repositório git dos plugins:
 - *Magenta:* Gráficos interativos 2D e painel de desenho
 - *JIFI:* Interpretador/editor de fluxogramas interativo
 - *DWRS:* Simulação de robôs baseados em Arduino
- Cria arquivo `build.xml` para [Ant]
- Compila tudo para a pasta `S3F_DIST`
- Executa

**Os repositórios contem projetos já configurados para [Netbeans], é só abrir e começar a programar!**


```sh
#!/bin/sh

#ANT BUILD FILE NAME
FILE="build.xml"

#MAIN DIST FOLDER
OUT_FOLDER="S3F_DIST"

#PLUGIN LIST
PLUGIN_NAMES="Magenta JIFI DWRS"

#JAR FILES USED
CLASSPATH="lib/autocomplete.jar lib/idw-gpl.jar lib/js.jar lib/rsyntaxtextarea.jar lib/jep-2.4.1.jar lib/RXTXcomm.jar"

#DOWLOAD S3F SOURCE
git clone --depth=1 https://github.com/anderson-/S3F.git

#DOWLOAD PLUGIN SOURCES
git clone --depth=1 https://github.com/anderson-/DWRS.git
git clone --depth=1 https://github.com/anderson-/Magenta.git
git clone --depth=1 https://github.com/anderson-/JIFI.git -b s3f_plugin

#CREATE/UPDATE ANT BUILD FILE
cat << START > ${FILE}
<?xml version="1.0" encoding="UTF-8"?>
<project name="S3F" basedir="." default="compile-plugins">
    <target name="init">
        <mkdir dir="${OUT_FOLDER}/lib"/>
        <mkdir dir="${OUT_FOLDER}/plugins"/>
        <mkdir dir="${OUT_FOLDER}/data"/>
        <mkdir dir="${OUT_FOLDER}/myScripts"/>
    </target>
    
    <target name="compile-S3F" depends="init" description="compile the main project">
        <ant antfile="nbbuild.xml" dir="S3F">
            <target name="jar" />
        </ant>
        <copy file="S3F/dist/S3F.jar" todir="${OUT_FOLDER}" />
        <copy todir="${OUT_FOLDER}/lib" failonerror="false">
            <fileset dir="S3F/dist/lib" />
        </copy>
	     <jar update="true" file="${OUT_FOLDER}/S3F.jar">
                <manifest>
                    <attribute name="Class-Path" value="${CLASSPATH}"/>
                </manifest>
            </jar>
    </target>
    
    <macrodef name="compile-plugin">
        <attribute name="project-name" default=""/>
        <sequential>
            <echo>Limpando e compilando @{project-name}...</echo>
            <ant antfile="nbbuild.xml" dir="@{project-name}">
                <target name="jar" />
            </ant>
            <copy file="@{project-name}/dist/@{project-name}.jar" todir="${OUT_FOLDER}/plugins" />
            <copy todir="${OUT_FOLDER}/lib" failonerror="false">
                <fileset dir="@{project-name}/dist/lib" />
            </copy>
        </sequential>
    </macrodef>
    
    <target name="compile-plugins" depends="compile-S3F" description="compile all the plugins">
START

#ANT TASK: COMPILE PLUGINS
for PLUGIN in ${PLUGIN_NAMES}; do
	echo "        <compile-plugin project-name=\"${PLUGIN}\"/>" >> ${FILE}
done

#ANT TASK: REMOVE DUPLICATE JAR FILES
for PLUGIN in ${PLUGIN_NAMES}; do
	echo "        <delete file=\"${OUT_FOLDER}/lib/${PLUGIN}.jar\"/>" >> ${FILE}
done

cat << END >> ${FILE}
    </target>
    
    <target name="run" depends="compile-plugins" description="run the main project with plugins">
        <java jar="${OUT_FOLDER}/S3F.jar" fork="true">
	    <classpath>
	        <fileset dir="${OUT_FOLDER}/lib">
		  <include name="*.jar"/>
	        </fileset>
            </classpath>
        </java>
    </target>

</project>
END

#RUN BUILD & RUN
ant -buildfile ${FILE} run

```

**Modificações uteis:**
- Modifique a variável `PLUGIN_NAMES` para indicar quais plugins serão compilados e integrados
- Modifique a variável `CLASSPATH` para incluir ou remover bibliotecas usadas
- Modifique a seção `#DOWLOAD PLUGIN SOURCES` para indicar quais repositórios serão baixados

**Notas:**
- Rodar `ant -buildfile build.xml run` para compilar e executar o programa posteriormente não exclui os arquivos/plugins adicionados anteriormente à pasta `S3F_DIST`, esses devem ser removidos manualmente.


License
----

GPLv3

*Copyright (C) 2014 Anderson de Oliveira Antunes <<anderson.utf@gmail.com>>*

[jifi]:https://github.com/anderson-/JIFI
[peteco]:http://dainf.ct.utfpr.edu.br/peteco/cursos/robotica
[pet]:http://portal.mec.gov.br/pet
[utfpr]:http://www.utfpr.edu.br
[arduino]:http://www.arduino.cc
[ant]:http://ant.apache.org
[netbeans]:https://netbeans.org
