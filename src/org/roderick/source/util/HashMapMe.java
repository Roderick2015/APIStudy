package org.roderick.source.util;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public class HashMapMe<K, V> extends AbstractMapMe<K, V>
	implements MapMe<K, V>, Cloneable, Serializable {
	
	private static final long serialVersionUID = -6449487488242541901L;

	//��ʼ������������2���ݣ�ΪʲôҪλ���㲻ֱ�Ӹ�ֵ��2���ݶ���Ϊ�˼���key֮�����ײ
	static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
	
	//���������������2���ݣ�������bucket������
	static final int MAXIMUM_CAPACITY = 1 << 30;
	
	//������ӣ�Ӱ���ϣ���Զ����ݵĴ�С
	static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	//��bucket�ϵĽ�����������ֵʱ��ת�ɺ����
	static final int TREEIFY_THRESHOLD = 8;
	
	//��bucket�ϵĽ����С�����ֵʱ��ת������
	static final int UNTREEIFY_THRESHOLD = 6;
	
	//������С����
	static final int MIN_TREEIFY_CAPACITY = 64;
	
	static class Node<K, V> implements MapMe.EntryMe<K, V> {
		final int hash; //final���������볣����
		final K key;
		V value;
		Node<K, V> next; //�¸�Ԫ�أ���������
		
		Node(int hash, K key, V value, Node<K, V> next) {
			this.hash = hash;
			this.key = key;
			this.value = value;
			this.next = next;
		}
		
		@Override
		public final K getKey() { //final����������ʱ��תΪ��Ƕ���ã���ʹ�ó����ѹջ��ʽ
			return key;
		}

		@Override
		public final V getValue() {
			return value;
		}

		@Override
		public final String toString() {
			return key + "=" + value;
		}
		
		@Override
		public final int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}
		
		@Override
		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}
		
		@Override
		public final boolean equals(Object o) {
			if (o == this)
				return true;
			if (o instanceof EntryMe) {
				MapMe.EntryMe<?, ?> e = (MapMe.EntryMe<?, ?>) o;
				if (Objects.equals(key, e.getKey()) &&
					Objects.equals(value, e.getValue()))
					return true;
			}
			return false;
		}
	}
	
	/**
	 * �洢Ԫ�ص����飬2���ݴα���bucket����
	 */
	transient Node<K, V>[] table;
	/**
	 * ��ž���Ԫ�صļ���
	 */
	transient SetMe<MapMe.EntryMe<K, V>> entrySet;
	transient int size; //��ӵ�Ԫ�ظ���
	transient int modCount;
	/**
	 * ��map��size(����*�������)���ڸ�ֵʱ����ʹ��resize��������
	 */
	transient int threshold;
	final float loadFactor;
	
	public HashMapMe(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) 
			throw new IllegalArgumentException("Illegal initial capacity: " +
                    							initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		if (loadFactor <= 0 || Float.isNaN(loadFactor)) //�ж�һ����������
			throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
		this.loadFactor = loadFactor;
		this.threshold = tableSizeFor(initialCapacity);
	}
	
	public HashMapMe(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	public HashMapMe() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
	}
	
	public HashMapMe(MapMe<? extends K, ? extends V> m) {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		
	}
	
	@Override
	public SetMe<MapMe.EntryMe<K, V>> entrySet() {
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" }) // for cast to Comparable
	static int compareComparables(Class<?> kc, Object k, Object x) {
		return (x == null || x.getClass() != kc ? 0 : ((Comparable) k).compareTo(x));
	}
	 
	/**
	 * ���ش��ڸ�����������С�Ķ�������ֵ
	 * Ϊʲô��
	 */
	static final int tableSizeFor(int cap) {
		int n = cap - 1;
		n |= n >>> 1;
		n |= n >>> 2;
		n |= n >>> 4;
		n |= n >>> 8;
		n |= n >>> 16;
		return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	}
	
	/**
	 * @param evict ��ɶ�ã�
	 */
	final void putMapEntries(MapMe<? extends K, ? extends V> m, boolean evict) {
		int s = size();
		if (s > 0) {
			if (table == null) { //��ϣ����������
				float ft = ((float)s / loadFactor) + 1.0F; //�õ�s���������(ʵ�ʴ�СΪ����*�������)
				int t = (ft < (float)MAXIMUM_CAPACITY) ? (int)ft : MAXIMUM_CAPACITY;
				if (t > threshold) //�������������Զ����ݵ���������t�Ĵ�С�޸�
					threshold = tableSizeFor(t);
			} else if (s > threshold) //?
				resize();
			for (org.roderick.source.util.MapMe.EntryMe<? extends K, ? extends V> e : m.entrySet()) {
				K key = e.getKey();
				V value = e.getValue();
				putVal(hash(key), key, value, false, evict);
			}
		}
	}
	
	public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }
	
	public V remove(Object key) {
        Node<K,V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
            null : e.value;
    }
	
	/**
	 * @param matchValue ?
	 * @param movable ?
	 * @return
	 */
	final Node<K, V> removeNode(int hash, Object key, Object value, boolean matchValue, boolean movable) {
		Node<K, V>[] tab;
		Node<K, V> p;
		int n, index;
		if ((tab = table) != null && (n = tab.length) > 0 && (p = tab[index = (n - 1) & hash]) != null) {
			Node<K, V> node = null, e;
			K k;
			V v;
			if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))
				node = p;
			else if ((e = p.next) != null) {
				if (p instanceof TreeNode)
					node = ((TreeNode<K, V>) p).getTreeNode(hash, key);
				else {
					do {
						if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) {
							node = e;
							break;
						}
						p = e;
					} while ((e = e.next) != null);
				}
			}
			if (node != null && (!matchValue || (v = node.value) == value || (value != null && value.equals(v)))) {
				if (node instanceof TreeNode)
					((TreeNode<K, V>) node).removeTreeNode(this, tab, movable);
				else if (node == p)
					tab[index] = node.next;
				else
					p.next = node.next;
				++modCount;
				--size;
				afterNodeRemoval(node);
				return node;
			}
		}
		return null;
	}
	
	final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
	
	public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

	static final int hash(Object key) {
		int h;
		return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16); //ɢ�з�ʽ��1.7��һ������μ�����key֮�����ײ
	}
	
	static Class<?> comparableClassFor(Object x) {
		if (x instanceof Comparable) {
			Class<?> c;
			Type[] ts;
			Type[] as;
			Type t;
			ParameterizedType p;
			if ((c = x.getClass()) == String.class)
				return c;
			if ((ts = c.getGenericInterfaces()) != null) {
				for (int i = 0; i < ts.length; ++i) {
					if (((t = ts[i]) instanceof ParameterizedType) &&
						((p = (ParameterizedType) t).getRawType() == 
						Comparable.class) &&
						(as = p.getActualTypeArguments()) != null &&
						as.length == 1 && as[0] == c)
						return c;
				}
			}
		}
		return null;
	}
	
	final void treeifyBin(Node<K,V>[] tab, int hash) {
        int n, index; Node<K,V> e;
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
            resize();
        else if ((e = tab[index = (n - 1) & hash]) != null) {
            TreeNode<K,V> hd = null, tl = null;
            do {
                TreeNode<K,V> p = replacementTreeNode(e, null);
                if (tl == null)
                    hd = p;
                else {
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            if ((tab[index] = hd) != null)
                hd.treeify(tab);
        }
    }

	final Node<K, V>[] resize() {
		Node<K, V>[] oldTab = table;
		int oldCap = (oldTab == null) ? 0 : oldTab.length;
		int oldThr = threshold;
		int newCap = 0;
		int newThr = 0;
		if (oldCap > 0) {
			if (oldCap >= MAXIMUM_CAPACITY) {
				threshold = Integer.MAX_VALUE;
				return oldTab; //�Ѵ����ֵ�޷�����
			} else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
					oldCap >= DEFAULT_INITIAL_CAPACITY) //�����ǰ��������������
				newThr = oldThr << 1; //����������
		} else if (oldThr > 0) { //�������Ϊ0����threshold����0��˵����ʼ����
			newCap = oldThr;
		} else { //δ��ʼ����
			newCap = DEFAULT_INITIAL_CAPACITY;
			newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
		}
		if (newThr == 0) { //�����thresholdΪ0��������µ�size�������������
			float ft = (float) newCap * loadFactor;
			newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
					  (int) ft : Integer.MAX_VALUE);
		}
		threshold = newThr;
		@SuppressWarnings("unchecked")
		Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
		table = newTab;
		if (oldTab != null) {
			for (int j = 0; j < oldCap; ++j) { //���ݺ󣬶Ծ�table�е�Ԫ������hash���䣬���������table��
				Node<K, V> e;
				if ((e = oldTab[j]) != null) {
					oldTab[j] = null;
					if (e.next == null)
						newTab[e.hash & (newCap - 1)] = e; //ֻ��һ��Ԫ�أ�������ֱ�Ӹ�����table�ĳ����ٴ�hash����
					else if (e instanceof TreeNode)
						((TreeNode<K, V>) e).split(this, newTab, j, oldCap);
					else {
						Node<K,V> loHead = null;
						Node<K,V> loTail = null;
						Node<K,V> hiHead = null;
						Node<K,V> hiTail = null;
						Node<K,V> next;
						do {
							next = e.next;
							if ((e.hash & oldCap) == 0) { //��������ʹ�ø������������֣��ֳ���������
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead; //����������ԭλ��
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead; //��ֳ�������λ��=��λ��+�ɱ���
						}
					}
				}
			}
		}
		return newTab;
		
	}
	
	public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
	
	final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
             boolean evict) {
		/*if (hash == 50)
		System.out.println("key:" + key + " hash:" + key.hashCode() + " size:" + size);*/
		Node<K, V>[] tab;
		Node<K, V> p;
		int n;
		int i;
		
		if ((tab = table) == null || (n = tab.length) == 0) //n��ֵΪ��ǰ����ĳ���
            n = (tab = resize()).length;
        if ((p = tab[i = 10]) == null) //i = (n - 1) & hash
        	//(n - 1) & hash ��ϣֵ��(���鳤��-1)�������㣬�˴����������ɽ���hash��ײ���Ը��Ӿ��ȵķֲ���bucket��
        	//����һ�������ǲ�Խ�磬�õ���ֵ�����n-1��
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
	}
	
	Node<K,V> replacementNode(Node<K,V> p, Node<K,V> next) {
        return new Node<>(p.hash, p.key, p.value, next);
    }
	
	Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        return new Node<>(hash, key, value, next);
    }
	
	TreeNode<K,V> newTreeNode(int hash, K key, V value, Node<K,V> next) {
        return new TreeNode<>(hash, key, value, next);
    }
	
	TreeNode<K, V> replacementTreeNode(Node<K, V> p, Node<K, V> next) {
		return new TreeNode<>(p.hash, p.key, p.value, next);
	}
	
	/**
	 * ���³�ʼ��
	 */
	void reinitialize() {
        table = null;
        entrySet = null;
        keySet = null;
        values = null;
        modCount = 0;
        threshold = 0;
        size = 0;
    }

	//callback?
	void afterNodeAccess(Node<K, V> p) {
	}

	void afterNodeInsertion(boolean evict) {
	}

	void afterNodeRemoval(Node<K, V> p) {
	}

	static final class TreeNode<K, V> extends LinkedHashMapMe.Entry<K,V> {
		TreeNode<K, V> parent;
		TreeNode<K, V> left;
		TreeNode<K, V> right;
		TreeNode<K, V> prev; //?
		boolean red;
		
		TreeNode(int hash, K key, V value, Node<K, V> next) {
			super(hash, key, value, next);
		}
		
		/**
		 * ���ذ�������ڵ�ĸ��ڵ�
		 */
		final TreeNode<K, V> root() {
			for (TreeNode<K, V> r = this, p;;) {
				if ((p = r.parent) == null) //Ѱ���Ҹ��ڵ㣬ֱ�����ڵ�
					return r;
				r = p;
			}
		}
		
		/**
		 * �ø�����root��Ϊtab�ĵ�һ���ڵ㣨���ڵ㣩
		 */
		static <K, V> void moveRootToFront(Node<K, V>[] tab, TreeNode<K, V> root) {
			int n;
			if (root != null && tab != null && (n = tab.length) > 0) {
				int index = (n - 1) & root.hash; //?
				TreeNode<K, V> first = (TreeNode<K, V>) tab[index];
				if (root != first) {
					Node<K, V> rn;
					tab[index] = root;
					TreeNode<K, V> rp = root.prev;
					if ((rn = root.next) != null)
						((TreeNode<K, V>)rn).prev = rp;
					if (rp != null)
						rp.next = rn;
					if (first != null)
						first.prev = root;  //?
					root.next = first;
					root.prev = null;
				}
				assert checkInvariants(root);
			}
		}
		
		final void split(HashMapMe<K, V> map, Node<K, V>[] tab, int index, int bit) {
			TreeNode<K, V> b = this;
			TreeNode<K, V> loHead = null;
			TreeNode<K, V> loTail = null;
			TreeNode<K, V> hiHead = null;
			TreeNode<K, V> hiTail = null;
			int lc = 0;
			int hc = 0;
			
			for (TreeNode<K, V> e = b, next; e != null; e = next) {
				next = (TreeNode<K, V>) e.next;
				e.next = null;
				if ((e.hash & bit) == 0) { //���Ǹ�ɶ?
					if ((e.prev = loTail) == null)
						loHead = e;
					else 
						loTail.next = e;
					loTail = e; //?
					++lc;
				} else {
					if ((e.prev = hiTail) == null)
						hiHead = e;
					else
						hiTail.next = e;
					hiTail = e;
					++hc;
				}
			}
			
			if (loHead != null) {
                if (lc <= UNTREEIFY_THRESHOLD)
                    tab[index] = loHead.untreeify(map);
                else {
                    tab[index] = loHead;
                    if (hiHead != null) // (else is already treeified)
                        loHead.treeify(tab);
                }
            }
            if (hiHead != null) {
                if (hc <= UNTREEIFY_THRESHOLD)
                    tab[index + bit] = hiHead.untreeify(map);
                else {
                    tab[index + bit] = hiHead;
                    if (loHead != null)
                        hiHead.treeify(tab);
                }
            }
		}
		
		final void removeTreeNode(HashMapMe<K, V> map, Node<K, V>[] tab, boolean movable) {
			int n;
			if (tab == null || (n = tab.length) == 0)
				return;
			int index = (n - 1) & hash;
			TreeNode<K, V> first = (TreeNode<K, V>) tab[index], root = first, rl;
			TreeNode<K, V> succ = (TreeNode<K, V>) next, pred = prev;
			if (pred == null)
				tab[index] = first = succ;
			else
				pred.next = succ;
			if (succ != null)
				succ.prev = pred;
			if (first == null)
				return;
			if (root.parent != null)
				root = root.root();
			if (root == null || root.right == null || (rl = root.left) == null || rl.left == null) {
				tab[index] = first.untreeify(map); // too small
				return;
			}
			TreeNode<K, V> p = this, pl = left, pr = right, replacement;
			if (pl != null && pr != null) {
				TreeNode<K, V> s = pr, sl;
				while ((sl = s.left) != null) // find successor
					s = sl;
				boolean c = s.red;
				s.red = p.red;
				p.red = c; // swap colors
				TreeNode<K, V> sr = s.right;
				TreeNode<K, V> pp = p.parent;
				if (s == pr) { // p was s's direct parent
					p.parent = s;
					s.right = p;
				} else {
					TreeNode<K, V> sp = s.parent;
					if ((p.parent = sp) != null) {
						if (s == sp.left)
							sp.left = p;
						else
							sp.right = p;
					}
					if ((s.right = pr) != null)
						pr.parent = s;
				}
				p.left = null;
				if ((p.right = sr) != null)
					sr.parent = p;
				if ((s.left = pl) != null)
					pl.parent = s;
				if ((s.parent = pp) == null)
					root = s;
				else if (p == pp.left)
					pp.left = s;
				else
					pp.right = s;
				if (sr != null)
					replacement = sr;
				else
					replacement = p;
			} else if (pl != null)
				replacement = pl;
			else if (pr != null)
				replacement = pr;
			else
				replacement = p;
			if (replacement != p) {
				TreeNode<K, V> pp = replacement.parent = p.parent;
				if (pp == null)
					root = replacement;
				else if (p == pp.left)
					pp.left = replacement;
				else
					pp.right = replacement;
				p.left = p.right = p.parent = null;
			}

			TreeNode<K, V> r = p.red ? root : balanceDeletion(root, replacement);

			if (replacement == p) { // detach
				TreeNode<K, V> pp = p.parent;
				p.parent = null;
				if (pp != null) {
					if (p == pp.left)
						pp.left = null;
					else if (p == pp.right)
						pp.right = null;
				}
			}
			if (movable)
				moveRootToFront(tab, r);
		}
		static int tieBreakOrder(Object a, Object b) {
            int d;
            if (a == null || b == null ||
                (d = a.getClass().getName().
                 compareTo(b.getClass().getName())) == 0)
                d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
                     -1 : 1);
            return d;
        }
		
		final void treeify(Node<K, V>[] tab) {
			TreeNode<K, V> root = null;
			for (TreeNode<K, V> x = this, next; x != null; x = next) {
				next = (TreeNode<K, V>) x.next;
				x.left = x.right = null;
				if (root == null) { //��һ���ڵ���Ϊ���ڵ�
					x.parent = null;
					x.red = false;
					root = x;
				} else {
					K k = x.key;
					int h = x.hash;
					Class<?> kc = null;
					for (TreeNode<K, V> p = root;;) {
						int dir;
						int ph;
						K pk = p.key;
						if ((ph = p.hash) > h)
							dir = -1;
						else if (ph < h) 
							dir = 1;
						else if ((kc == null && 
								 (kc = comparableClassFor(k)) == null) ||
								 (dir = compareComparables(kc, k, pk)) == 0)
							dir = tieBreakOrder(k, pk);
						
						TreeNode<K, V> xp = p;
						if ((p = (dir <= 0) ? p.left : p.right) == null) {
							x.parent = xp;
							if (dir <= 0)
								xp.left = x;
							else 
								xp.right = x;
							root = balanceInsertion(root, x);
							break;
						}
					}
				}
			}
			moveRootToFront(tab, root);
		}
		
		final Node<K, V> untreeify(HashMapMe<K, V> map) {
			Node<K, V> hd = null;
			Node<K, V> tl = null;
			for (Node<K, V> q = this; q != null; q = q.next) {
				Node<K, V> p = map.replacementNode(q, null);
				if (tl == null)
					hd = p;
				else
					tl.next = p;
				tl = p;
			}
			return hd;
		}

		final TreeNode<K, V> putTreeVal(HashMapMe<K, V> map, Node<K, V>[] tab, int h, K k, V v) {
			Class<?> kc = null;
			boolean searched = false;
			TreeNode<K, V> root = (parent != null) ? root() : this;
			for (TreeNode<K, V> p = root;;) {
				int dir, ph;
				K pk;
				if ((ph = p.hash) > h)
					dir = -1;
				else if (ph < h)
					dir = 1;
				else if ((pk = p.key) == k || (k != null && k.equals(pk)))
					return p;
				else if ((kc == null && (kc = comparableClassFor(k)) == null)
						|| (dir = compareComparables(kc, k, pk)) == 0) {
					if (!searched) {
						TreeNode<K, V> q, ch;
						searched = true;
						if (((ch = p.left) != null && (q = ch.find(h, k, kc)) != null)
								|| ((ch = p.right) != null && (q = ch.find(h, k, kc)) != null))
							return q;
					}
					dir = tieBreakOrder(k, pk);
				}

				TreeNode<K, V> xp = p;
				if ((p = (dir <= 0) ? p.left : p.right) == null) {
					Node<K, V> xpn = xp.next;
					TreeNode<K, V> x = map.newTreeNode(h, k, v, xpn);
					if (dir <= 0)
						xp.left = x;
					else
						xp.right = x;
					xp.next = x;
					x.parent = x.prev = xp;
					if (xpn != null)
						((TreeNode<K, V>) xpn).prev = x;
					moveRootToFront(tab, balanceInsertion(root, x));
					return null;
				}
			}
		}
		
		final TreeNode<K,V> find(int h, Object k, Class<?> kc) {
            TreeNode<K,V> p = this;
            do {
                int ph, dir; K pk;
                TreeNode<K,V> pl = p.left, pr = p.right, q;
                if ((ph = p.hash) > h)
                    p = pl;
                else if (ph < h)
                    p = pr;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                    return p;
                else if (pl == null)
                    p = pr;
                else if (pr == null)
                    p = pl;
                else if ((kc != null ||
                          (kc = comparableClassFor(k)) != null) &&
                         (dir = compareComparables(kc, k, pk)) != 0)
                    p = (dir < 0) ? pl : pr;
                else if ((q = pr.find(h, k, kc)) != null)
                    return q;
                else
                    p = pl;
            } while (p != null);
            return null;
        }
		
		final TreeNode<K,V> getTreeNode(int h, Object k) {
            return ((parent != null) ? root() : this).find(h, k, null);
        }

		static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root, TreeNode<K, V> p) {
			TreeNode<K, V> r, pp, rl;
			if (p != null && (r = p.right) != null) {
				if ((rl = p.right = r.left) != null)
					rl.parent = p;
				if ((pp = r.parent = p.parent) == null)
					(root = r).red = false;
				else if (pp.left == p)
					pp.left = r;
				else
					pp.right = r;
				r.left = p;
				p.parent = r;
			}
			return root;
		}

		static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root, TreeNode<K, V> p) {
			TreeNode<K, V> l, pp, lr;
			if (p != null && (l = p.left) != null) {
				if ((lr = p.left = l.right) != null)
					lr.parent = p;
				if ((pp = l.parent = p.parent) == null)
					(root = l).red = false;
				else if (pp.right == p)
					pp.right = l;
				else
					pp.left = l;
				l.right = p;
				p.parent = l;
			}
			return root;
		}

		static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root, TreeNode<K, V> x) {
			x.red = true;
			for (TreeNode<K, V> xp, xpp, xppl, xppr;;) {
				if ((xp = x.parent) == null) {
					x.red = false;
					return x;
				} else if (!xp.red || (xpp = xp.parent) == null)
					return root;
				if (xp == (xppl = xpp.left)) {
					if ((xppr = xpp.right) != null && xppr.red) {
						xppr.red = false;
						xp.red = false;
						xpp.red = true;
						x = xpp;
					} else {
						if (x == xp.right) {
							root = rotateLeft(root, x = xp);
							xpp = (xp = x.parent) == null ? null : xp.parent;
						}
						if (xp != null) {
							xp.red = false;
							if (xpp != null) {
								xpp.red = true;
								root = rotateRight(root, xpp);
							}
						}
					}
				} else {
					if (xppl != null && xppl.red) {
						xppl.red = false;
						xp.red = false;
						xpp.red = true;
						x = xpp;
					} else {
						if (x == xp.left) {
							root = rotateRight(root, x = xp);
							xpp = (xp = x.parent) == null ? null : xp.parent;
						}
						if (xp != null) {
							xp.red = false;
							if (xpp != null) {
								xpp.red = true;
								root = rotateLeft(root, xpp);
							}
						}
					}
				}
			}
		}

		static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root, TreeNode<K, V> x) {
			for (TreeNode<K, V> xp, xpl, xpr;;) {
				if (x == null || x == root)
					return root;
				else if ((xp = x.parent) == null) {
					x.red = false;
					return x;
				} else if (x.red) {
					x.red = false;
					return root;
				} else if ((xpl = xp.left) == x) {
					if ((xpr = xp.right) != null && xpr.red) {
						xpr.red = false;
						xp.red = true;
						root = rotateLeft(root, xp);
						xpr = (xp = x.parent) == null ? null : xp.right;
					}
					if (xpr == null)
						x = xp;
					else {
						TreeNode<K, V> sl = xpr.left, sr = xpr.right;
						if ((sr == null || !sr.red) && (sl == null || !sl.red)) {
							xpr.red = true;
							x = xp;
						} else {
							if (sr == null || !sr.red) {
								if (sl != null)
									sl.red = false;
								xpr.red = true;
								root = rotateRight(root, xpr);
								xpr = (xp = x.parent) == null ? null : xp.right;
							}
							if (xpr != null) {
								xpr.red = (xp == null) ? false : xp.red;
								if ((sr = xpr.right) != null)
									sr.red = false;
							}
							if (xp != null) {
								xp.red = false;
								root = rotateLeft(root, xp);
							}
							x = root;
						}
					}
				} else { // symmetric
					if (xpl != null && xpl.red) {
						xpl.red = false;
						xp.red = true;
						root = rotateRight(root, xp);
						xpl = (xp = x.parent) == null ? null : xp.left;
					}
					if (xpl == null)
						x = xp;
					else {
						TreeNode<K, V> sl = xpl.left, sr = xpl.right;
						if ((sl == null || !sl.red) && (sr == null || !sr.red)) {
							xpl.red = true;
							x = xp;
						} else {
							if (sl == null || !sl.red) {
								if (sr != null)
									sr.red = false;
								xpl.red = true;
								root = rotateLeft(root, xpl);
								xpl = (xp = x.parent) == null ? null : xp.left;
							}
							if (xpl != null) {
								xpl.red = (xp == null) ? false : xp.red;
								if ((sl = xpl.left) != null)
									sl.red = false;
							}
							if (xp != null) {
								xp.red = false;
								root = rotateRight(root, xp);
							}
							x = root;
						}
					}
				}
			}
		}
		
		/**
		 * �ݹ���֤����֤ɶ?
		 */
		static <K, V> boolean checkInvariants(TreeNode<K, V> t) {
			TreeNode<K, V> tp = t.parent;
			TreeNode<K, V> tl = t.left;
			TreeNode<K, V> tr = t.right;
			TreeNode<K, V> tb = t.prev;
			TreeNode<K, V> tn = (TreeNode<K, V>) t.next; //next:TreeNode����ĸ��࣬����ֻص�hashmap
			
			if (tb != null && tb.next != t) 
				return false;
			if (tn != null && tn.prev != t)
				return false;
			if (tp != null && t != tp.left && t != tp.right)
                return false;
            if (tl != null && (tl.parent != t || tl.hash > t.hash))
                return false;
            if (tr != null && (tr.parent != t || tr.hash < t.hash))
                return false;
            if (t.red && tl != null && tl.red && tr != null && tr.red)
                return false;
            if (tl != null && !checkInvariants(tl))
                return false;
            if (tr != null && !checkInvariants(tr))
                return false;
            return true;
		}
	}
}
