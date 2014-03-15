#!/bin/bash
#
#       create_c_files.sh
#       
#       Copyright 2011 Anderson de Oliveira Antunes <anderson.utf@gmail.com>
#       
#       This program is free software; you can redistribute it and/or modify
#       it under the terms of the GNU General Public License as published by
#       the Free Software Foundation; either version 2 of the License, or
#       (at your option) any later version.
#       
#       This program is distributed in the hope that it will be useful,
#       but WITHOUT ANY WARRANTY; without even the implied warranty of
#       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#       GNU General Public License for more details.
#       
#       You should have received a copy of the GNU General Public License
#       along with this program; if not, write to the Free Software
#       Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
#       MA 02110-1301, USA.

zenity --info --title="Create XC Files" --text="Bem Vindo!\n\n	Este script cria automaticamente os arquivos .java para um projeto.\n	Será exibida uma janela com um campo de inserção de texto, insira o nome das bibliotecas a serem criadas, sem a extenção .h e com espaços (' ') entre eles"

arquivos=$(zenity --text-info --title="Create C Files" --editable)

pacote=$(zenity --entry --text "pacote" --entry-text "");

while IFS='.' read -ra ADDR; do
    for i in "${ADDR[@]}"; do
        if [ ! -d "$i" ]; then
            mkdir "$i"
        fi
        cd "$i"
    done
done <<< "$pacote"

for arq in $arquivos; do
	cat << LIB > ${arq}.java
/**
 * ${arq}.java
 *
 * Copyright (C) 2014
 *
 *       Anderson de Oliveira Antunes <anderson.utf@gmail.com>
 *
 * This file is part of S3F.
 *
 * S3F is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * S3F is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * S3F. If not, see http://www.gnu.org/licenses/.
 */

package ${pacote};

public class ${arq} {
	
}

LIB

done

