#!/usr/bin/zsh
for i in 16 32 50
do 
	mkdir -p icons/${i}/gray
	for f in icons/svg/*
	do
		w=${i}
		if [[ ${f:t:r} = "Arrow_facing_right_-_Green" ]]
		then
			w=$((${i}*2))
		fi
		inkscape -f ${f} -w ${w} -h ${i} -e icons/${i}/${f:t:r}.png
		convert icons/${i}/${f:t:r}.png -colorspace Gray icons/${i}/gray/${f:t:r}.png
	done
done

zip -r ../resources.zip icons license
