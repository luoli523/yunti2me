import requests
import xml.etree.ElementTree as ET
import argparse

def parse_args():
  parser = argparse.ArgumentParser(description='get yarn nodes\' and their labels from resourcemanager\'s rest api')
  parser.add_argument('--rm_host', type=str, required=True, help='yarn resourcemanager\'s host')
  parser.add_argument('--rm_port', type=str, default='8088', help='yarn resourcemanager\'s web port')
  parser.add_argument('--path', type=str, default='/ws/v1/cluster/nodes', help='rest api path')
  return parser.parse_args()

def main(args):
  rmhost = args.rm_host
  rmport = args.rm_port
  rmpath = args.path
  rmurl = "http://%s:%s%s" % (rmhost, rmport, rmpath)
  header={'Accept':'application/xml'}
  response = requests.get(rmurl, headers=header)
  tree = ET.fromstring(response.text)

  for node in tree.findall('node'):
    nodeid = node.find('id').text
    label = node.find('nodeLabels')
    if label is None:
      print "%s\t%s"% (nodeid,label)
    else:
      print "%s\t%s"% (nodeid,label.text)

if __name__ == '__main__':
  args = parse_args()
  main(args)
