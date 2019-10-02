# Converting a csv file (from repast simulation) to shapefile . 
# Author: Swapna Thorve (st6ua@virginia.edu)


import pandas as pd
import geopandas as gpd
from shapely.geometry import Point
import fiona
import os.path
from os import path
import sys


repast_output_path = 'D://HHI2019//simulation-data//'  # csv path dir
file_out_dir = "D://HHI2019//simulation-data//shapefiles//" #shapefile path dir

no_agents = 70
start_point = 1

for i in range(start_point,no_agents+1):
    filename = repast_output_path + "run-1-pastoralist-" + str(i) + ".csv"
    if(path.exists(filename)):
        print('Processing ' + filename)
        data = pd.read_csv(filename)
        #print(data.head()) # debugging purpose
        data.columns = data.columns.str.strip()
        #print(data.columns) # debugging purpose
        geometry = [Point(xy) for xy in zip(data.lat, data.lon)]
        crs = fiona.crs.from_epsg(4326)
        geo_df = gpd.GeoDataFrame(data, crs=crs, geometry=geometry)
        outname = file_out_dir + "run-1-pastoralist-" + str(i) + ".shp"
        geo_df.to_file(driver='ESRI Shapefile', filename=outname)
    else:
        print('************ FILE NOT PRESENT : ' + filename)

